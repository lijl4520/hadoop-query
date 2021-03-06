/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.impl;

import com.huawei.commons.domain.ZkProperties;
import com.huawei.commons.exception.Asserts;
import com.huawei.commons.exception.QueryException;
import com.huawei.commons.TableCallback;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.util.Assert;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Hbase查询业务处理
 *
 * @since 2021/8/30
 */
@Slf4j
public class Hbase implements HbaseOperations {

    private Configuration configuration;

    private Admin hBaseAdmin;

    private volatile Connection connection;

    /**
     * 构造函数，每个集群创建一个Hbase对象
     *
     * @param hbaseDatasource
     */
    public Hbase(ZkProperties hbaseDatasource) {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.security.authentication", "kerberos");
        conf.set("hbase.regionserver.kerberos.principal", "hbase/_HOST@SCKDC");
        conf.set("hbase.zookeeper.quorum", hbaseDatasource.getZookeeperQuorum());
        conf.set("zookeeper.znode.parent", "/hbase-secure");
        this.setConfiguration(conf);
        //getConnection();
        Assert.notNull(conf,"a valid configuration is required");
    }

    @Override
    public <T> T execute(String tableName, TableCallback<T> action) {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(tableName,"No table specified");
        Table table = null;
        try {
            TableName table_name = TableName.valueOf(tableName);
            table = this.getConnection().getTable(table_name);
            boolean b = this.hBaseAdmin.tableExists(table_name);
            if (!b){
                Asserts.fail("表名不存在");
            }
            return action.doInTable(table);
        }catch (Exception e){
            throw new QueryException("其他错误");
        }finally {
            if (null!=table){
                try {
                    table.close();
                }catch (IOException e){
                    throw new QueryException("其他错误");
                }
            }
        }
    }

    /**
     * @Author lijiale
     * @MethodName find
     * @Description Hbase 查询
     * @Date 17:24 2021/10/25
     * @Version 1.0
     * @param tableName
     * @param startRowKey
     * @param endRowKey
     * @param filterVal
     * @param mapper
     * @return: java.util.List<T>
     **/
    @Override
    public <T> List<T> find(String tableName, String startRowKey, String endRowKey, String filterVal, RowMapper<T> mapper) {
        FilterList fl = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        RowFilter rf = new RowFilter(CompareOperator.EQUAL,new SubstringComparator(filterVal));
        fl.addFilter(rf);
        Scan scan = new Scan();
        scan.withStartRow(Bytes.toBytes(startRowKey));
        scan.withStopRow(Bytes.toBytes(endRowKey));
        scan.setFilter(fl);
        return this.find(tableName,scan,mapper);
    }

    /**
     * @Author lijiale
     * @MethodName find
     * @Description Hbase 查询
     * @Date 17:24 2021/10/25
     * @Version 1.0
     * @param tableName
     * @param startRowKey
     * @param endRowKey
     * @param action
     * @return: java.util.List<T>
    **/
    @Override
    public <T> List<T> find(String tableName, String startRowKey, String endRowKey, RowMapper<T> action) {
        FilterList fl = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        RegexStringComparator rc = new RegexStringComparator("[^\\\\\\/\\^]");
        RowFilter rf = new RowFilter(CompareOperator.EQUAL,rc);
        fl.addFilter(rf);
        Scan scan = new Scan();
        scan.withStartRow(Bytes.toBytes(startRowKey));
        scan.withStopRow(Bytes.toBytes(endRowKey), true);
        scan.setFilter(fl);
        return this.find(tableName,scan,action);
    }

    /**
     * @Author lijiale
     * @MethodName find
     * @Description hbase 查询
     * @Date 17:25 2021/10/25
     * @Version 1.0
     * @param tableName
     * @param scan
     * @param action
     * @return: java.util.List<T>
    **/
    @Override
    public <T> List<T> find(String tableName, Scan scan, RowMapper<T> action) {
        return this.execute(tableName, table -> {
            long startTime = System.currentTimeMillis();
            int caching = scan.getCaching();
            if (caching==1){
                scan.setCaching(10000);
            }
            ResultScanner resultScanner = null;
            try {
                resultScanner = table.getScanner(scan);
                Iterator<Result> resultIterator = resultScanner.iterator();
                List<T> rs = new ArrayList<T>();
                while (resultIterator.hasNext()) {
                    Result next = resultIterator.next();
                    rs.add(action.mapRow(next));
                }
                long endTime = System.currentTimeMillis();
                log.info("线程:{}--->查询Hbase:{}表，共查询了{}条数据--->耗时:{}",Thread.currentThread().getName(),tableName,rs.size(),endTime-startTime);
                return rs;
            }finally {
                resultScanner.close();
            }
        });
    }

    public Connection getConnection() {
        if (null==this.connection){
            synchronized (this){
                if (null==this.connection){
                    try {
                        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(200, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
                        poolExecutor.prestartCoreThread();
                        this.connection = ConnectionFactory.createConnection(getConfiguration(),poolExecutor);
                        this.hBaseAdmin = this.connection.getAdmin();
                    }catch (Exception e){
                        log.error("hbase connection资源池创建失败");
                        new QueryException("hbase connection资源池创建失败");
                    }
                }
            }
        }
        return this.connection;
    }

    @Override
    public Hbase closeConnection(){
        if (this.connection!=null){
            try {
                this.connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
