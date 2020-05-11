package com.ssxlulu.executor;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Execute Engine.
 *
 * @author ssxlulu
 */
@Slf4j
public final class ExecuteEngine {

    private final ListeningExecutorService executorService;

    public ExecuteEngine(final int maxWorkerThread) {
        executorService = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(maxWorkerThread, maxWorkerThread, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>()));
    }

    /**
     * Submit data check task.
     *
     * @param recorderCheckTask data check task
     */
    @SuppressWarnings("unchecked")
    public void submitTask(final RecorderCheckTask recorderCheckTask) {
        List<ListenableFuture<Object>> listenableFutures = new ArrayList<>();
        ListenableFuture listenableFuture = executorService.submit(recorderCheckTask.getRecorderReader());
        ListenableFuture listenableFuture2 = executorService.submit(recorderCheckTask.getRecorderChecker());
        listenableFutures.add(listenableFuture);
        listenableFutures.add(listenableFuture2);
        ListenableFuture alllistenableFuture = Futures.allAsList(listenableFutures);
        Futures.addCallback(alllistenableFuture, new FutureCallback<List<Object>>() {
            @Override
            public void onSuccess(final List<Object> result) {
                log.info("Table {} recorderCheck task finished!", recorderCheckTask.getTableName());
            }

            @Override
            public void onFailure(final Throwable t) {
                log.info("Table {} recorderCheck task failed!", recorderCheckTask.getTableName());
            }
        });
    }

    /**
     * Shutdown execute engine.
     */
    public void shutdown() {
        executorService.shutdown();
    }

    /**
     * Await terminate of execute engine.
     *
     * @return true if the execute engine terminated, otherwise false
     */
    @SneakyThrows
    public boolean awaitTermination() {
        return executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
    }
}
