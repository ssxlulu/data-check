package com.ssxlulu.executor;

/**
 * Data check executor.
 *
 * @author ssxlulu
 */
public interface Executor extends Runnable {
    /**
     * Start run execute.
     */
    void start();

    /**
     * Stop running execute.
     */
    void stop();
}
