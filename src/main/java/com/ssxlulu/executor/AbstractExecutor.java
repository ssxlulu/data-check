package com.ssxlulu.executor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Abstract of Executor.
 *
 * @author ssxlulu
 */
public abstract class AbstractExecutor implements Executor {

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private volatile boolean running;

    /**
     * generic start implement.
     */
    @Override
    public void start() {
        running = true;
    }

    /**
     * generic stop implement.
     */
    @Override
    public void stop() {
        running = false;
    }
}
