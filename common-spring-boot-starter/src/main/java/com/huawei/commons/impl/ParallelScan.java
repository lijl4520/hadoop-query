/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Author Lijl
 * @ClassName ParallelScan
 * @Description TODO
 * @Date 2022/2/13 9:56
 * @Version 1.0
 */
@Slf4j
public class ParallelScan {

    private static ThreadPoolExecutor scannersPool = null;
    private static final int CORE_THREADS = 256;
    private static final int MAX_THREADS = 256;
    private static final int KEEP_ALIVE_TIME_SEC = 600;

    private final Table table;
    private List<Scan> scans;
    private List<Result> results;
    private CompletionService<BlockingQueue<Result>> completionService;

    public ParallelScan(Connection conn, Table table, Scan scan) throws IOException {
        this(table, scan, getSplitKeysByMetaTable(conn, table.getName()));
    }

    public ParallelScan(Table table, Scan scan, List<byte[]> splitKeys) throws IOException {
        this.table = table;
        this.splitScansBySplitKeys(scan, splitKeys);
        this.threadPoolInitialize();
    }

    public List<Result> getResults() throws InterruptedException, ExecutionException {
        results = new ArrayList<>();
        for (int i = 0; i < this.scans.size(); i++) {
            BlockingQueue<Result> result = completionService.take().get();
            Iterator<Result> iterator = result.iterator();
            while (iterator.hasNext()) {
                results.add(iterator.next());
            }
        }
        return results;
    }

    private static List<byte[]> getSplitKeysByMetaTable(Connection conn, TableName tableName) throws IOException {
        List<byte[]> splitKeys = new ArrayList<>();
        RegionLocator regionLocator = conn.getRegionLocator(tableName);
        List<HRegionLocation> allRegionLocations = regionLocator.getAllRegionLocations();
        for (int i = 0; i < allRegionLocations.size(); i++) {
            HRegionInfo regionInfo = allRegionLocations.get(i).getRegionInfo();
            if (!Bytes.toString(regionInfo.getStartKey()).isEmpty()) {
                splitKeys.add(regionInfo.getStartKey());
            }
        }
        return splitKeys;
    }

    private void splitScansBySplitKeys(Scan scan, List<byte[]> splitKeys) throws IOException {
        this.scans = new ArrayList<>();
        byte[] splitStartKey = scan.getStartRow();
        byte[] stopRow = scan.getStopRow();
        Scan subScan;

        for (int i = 0; i < splitKeys.size(); i ++) {
            byte[] splitKey = splitKeys.get(i);

            subScan = new Scan(scan);
            subScan.setStartRow(splitStartKey);
            subScan.setStopRow(splitKey);
            splitStartKey = splitKey;
            this.scans.add(subScan);
        }
        subScan = new Scan(scan);
        subScan.setStartRow(splitStartKey);
        this.scans.add(subScan);
    }

    private void threadPoolInitialize() {
        scannersPool = getParallelScanThreadPool(CORE_THREADS, MAX_THREADS, KEEP_ALIVE_TIME_SEC,
                new LinkedBlockingQueue<>());
        completionService = new ExecutorCompletionService<>(scannersPool);
        log.info("Scan Size:{}",this.scans.size());
        try {
            for (int i = 0; i < this.scans.size(); i++) {
                ParallelScannerThread scannerThread = new ParallelScannerThread(scans.get(i));
                completionService.submit(scannerThread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ThreadPoolExecutor getParallelScanThreadPool(int coreThreads, int maxThreads,
                                                         long keepAliveTime,
                                                         BlockingQueue<Runnable> workQueue) {
        return new ThreadPoolExecutor(coreThreads,
                maxThreads,
                keepAliveTime,
                TimeUnit.SECONDS,
                workQueue);
    }

    private class ParallelScannerThread implements Callable<BlockingQueue<Result>> {

        private ResultScanner scanner;
        private BlockingQueue<Result> results;

        protected ParallelScannerThread(Scan scan) throws IOException {
            this.scanner = ParallelScan.this.table.getScanner(scan);
            this.results = new LinkedBlockingQueue<>();
        }

        @Override
        public BlockingQueue<Result> call() {
            try {
                Result result = this.scanner.next();
                while(result != null) {
                    boolean added = false;
                    while (!added) {
                        added = this.results.offer(result);
                    }
                    result = this.scanner.next();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                this.scanner.close();
            }
            return this.results;
        }
    }
}
