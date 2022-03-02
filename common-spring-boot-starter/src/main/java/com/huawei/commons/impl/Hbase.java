/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.impl;

import com.alibaba.fastjson.JSON;
import com.huawei.commons.ScanCallback;
import com.huawei.commons.domain.ZkProperties;
import com.huawei.commons.exception.Asserts;
import com.huawei.commons.exception.QueryException;
import com.huawei.commons.TableCallback;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

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
        conf.set("hadoop.security.authentication","kerberos");
        conf.set("hbase.security.authentication", "kerberos");
        conf.set("hbase.regionserver.kerberos.principal", hbaseDatasource.getPrincipal());
        conf.set("hbase.master.kerberos.principal", hbaseDatasource.getPrincipal());
        conf.set("hbase.zookeeper.quorum", hbaseDatasource.getZookeeperQuorum());
        conf.set("hbase.zookeeper.property.clientPort",hbaseDatasource.getZookeeperClientPort());
        conf.set("zookeeper.znode.parent", hbaseDatasource.getZnodeParent());
        String sitePath = hbaseDatasource.getSitePath();
        String corePath = hbaseDatasource.getCorePath();
        String hdfsPath = hbaseDatasource.getHdfsPath();
        if (sitePath!=null&&corePath!=null&&hdfsPath!=null){
            conf.addResource(new Path(sitePath));
            conf.addResource(new Path(corePath));
            conf.addResource(new Path(hdfsPath));
        }
        this.setConfiguration(conf);
        this.getConnection();
        Assert.notNull(conf,"a valid configuration is required");
    }

    @Override
    public <T> T execute(String tableName, TableCallback<T> action) {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(tableName,"No table specified");
        Table table = null;
        try {
            log.info("query >>>>>>>>>> TableName:>>>>>>>{}",tableName);
            TableName table_name = TableName.valueOf(tableName);
            table = this.getConnection().getTable(table_name);
            boolean b = this.hBaseAdmin.tableExists(table_name);
            if (!b){
                Asserts.fail("表名不存在");
            }
            return action.doInTable(table);
        }catch (Exception e){
            e.printStackTrace();
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

    @Override
    public <T> T execute(String tableName, Scan scan, ScanCallback<T> action) {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(tableName,"No table specified");
        Table table = null;
        try {
            log.info("query >>>>>>>>>> TableName:>>>>>>>{}",tableName);
            TableName table_name = TableName.valueOf(tableName);
            Connection connection = this.getConnection();
            table = connection.getTable(table_name);
            boolean b = this.hBaseAdmin.tableExists(table_name);
            if (!b){
                Asserts.fail("表名不存在");
            }
            return action.doInTable(connection,table,scan);
        }catch (Exception e){
            e.printStackTrace();
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
     * @Description 二级索引查询
     * @Date 17:00 2021/11/22
     * @Version 1.0
     * @param tableName 表名
     * @param family 列簇
     * @param qualifier 区间查询列
     * @param startVal 区间查询开始值
     * @param endVal 区间查询结束值
     * @param qualifierAndVal 二级索引查询列-值
     * @param rowVal 省份查询值
     * @param mapper
     * @return: java.util.List<T>
    **/
    @Override
    public <T> List<T> find(String tableName, String family, String qualifier, Long startVal,
                            Long endVal, Map<String,String> qualifierAndVal, String rowVal, final RowMapper<T> mapper) {
        FilterList fl = new FilterList();
        log.info("表名:{},列簇:{},区间查询列:{},区间开始时间戳:{},区间结束时间戳:{},其他过滤条件:{},地市条件:{}",
                tableName, family, qualifier, startVal, endVal,
                JSON.toJSONString(qualifierAndVal), rowVal);
        if (StringUtils.hasLength(rowVal)){
            log.info("地市编码--------》{}",rowVal);
            RowFilter rf = new RowFilter(CompareFilter.CompareOp.EQUAL,new SubstringComparator(rowVal));
            fl.addFilter(rf);
        }
        if (family!=null){
            qualifierAndVal.forEach((k,v)->{
                SingleColumnValueFilter singleColumnValueFilter = new SingleColumnValueFilter(Bytes.toBytes(family),
                        Bytes.toBytes(k),CompareFilter.CompareOp.EQUAL,Bytes.toBytes(v));
                singleColumnValueFilter.setFilterIfMissing(true);
                fl.addFilter(singleColumnValueFilter);
            });

            if (qualifier !=null && startVal!=null && endVal!=null){
                SingleColumnValueFilter startColumnValFilter = new SingleColumnValueFilter(Bytes.toBytes(family),
                        Bytes.toBytes(qualifier), CompareFilter.CompareOp.GREATER_OR_EQUAL, Bytes.toBytes(String.valueOf(startVal.longValue())));
                startColumnValFilter.setFilterIfMissing(true);
                fl.addFilter(startColumnValFilter);
                SingleColumnValueFilter endColumnValFilter = new SingleColumnValueFilter(Bytes.toBytes(family),
                        Bytes.toBytes(qualifier), CompareFilter.CompareOp.LESS_OR_EQUAL, Bytes.toBytes(String.valueOf(endVal.longValue())));
                endColumnValFilter.setFilterIfMissing(true);
                fl.addFilter(endColumnValFilter);
            }
            Scan scan = new Scan();
            scan.setFilter(fl);
            return this.execute(tableName,scan,(conn,tab,sc)->{
                List<T> rs = new ArrayList<T>();
                ParallelScan parallelScan = new ParallelScan(conn, tab, sc);
                List<Result> results = parallelScan.getResults();
                results.forEach(result ->{
                    try {
                        rs.add(mapper.mapRow(result));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return rs;
            });
        }
        return null;
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
            List<T> rs = new ArrayList<T>();
            try {
                resultScanner = table.getScanner(scan);
                Iterator<Result> resultIterator = resultScanner.iterator();
                while (resultIterator.hasNext()) {
                    Result next = resultIterator.next();
                    rs.add(action.mapRow(next));
                }
                long endTime = System.currentTimeMillis();
                log.info("线程:{}--->查询Hbase:{}表，共查询了{}条数据--->耗时:{}",Thread.currentThread().getName(),tableName,rs.size(),endTime-startTime);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                resultScanner.close();
            }
            return rs;
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
                log.info("关闭连接,{}",this.connection.getConfiguration());
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
