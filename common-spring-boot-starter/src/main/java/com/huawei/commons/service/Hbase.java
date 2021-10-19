/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.service;

import com.huawei.commons.domain.ZkProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Hbase查询业务处理
 *
 * @since 2021/8/30
 */
@Slf4j
public class Hbase {

    private Connection connection;

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
        try {
            connection = ConnectionFactory.createConnection(conf);
        } catch (Exception e) {
            log.error("create connection to hbase error:", e);
        }
    }

    /**
     * HBase查询方法
     *
     * @param tableNameStr tableNameStr
     * @param startRowKey  startRowKey
     * @param endRowKey    endRowKey
     * @return List<Result>
     */
    public List<Result> scanRowkeyRange(String tableNameStr, String startRowKey,
                                        String endRowKey) {
        long strtTime = System.currentTimeMillis();
        String threadName = Thread.currentThread().getName();
        log.info("***************  query table {} begin ***************ThreadName:{}*******", tableNameStr,threadName);
        TableName tableName = TableName.valueOf(tableNameStr);
        Table table = null;
        ResultScanner resultScanner = null;
        List<Result> resultList = new ArrayList<>();
        Iterator<Result> resultIterator;
        try {
            table = connection.getTable(tableName);
            Scan scan = new Scan();
            scan.withStartRow(Bytes.toBytes(startRowKey));
            scan.withStopRow(Bytes.toBytes(endRowKey), true);
            resultScanner = table.getScanner(scan);
            resultIterator = resultScanner.iterator();
            while (resultIterator.hasNext()) {
                resultList.add(resultIterator.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != resultScanner) {
                resultScanner.close();
            }
            if (null != table) {
                try {
                    table.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != connection){
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            log.info("***************  query table {} end *************** ThreadName:{} *******", tableNameStr,threadName);
            long endTime = System.currentTimeMillis();
            log.warn("*************** query table {} time consuming {} ms ***** ThreadName:{} ******",tableNameStr,endTime-strtTime,threadName);
        }
        return resultList;
    }
}
