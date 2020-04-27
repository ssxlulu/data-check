package com.ssxlulu.executor;

import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ssxlulu
 */
@Getter
public class ExecuteEngine {

    private final ExecutorService executorService;

    public ExecuteEngine(final int maxWorkerThread) {
        executorService = new ThreadPoolExecutor(maxWorkerThread, maxWorkerThread, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

}
