package com.feeedify.rest.service.favicon;

import java.util.concurrent.Executor;

/** Runs tasks immediately on the current thread. */
final class SynchronousExecutor implements Executor {
    private static final SynchronousExecutor singleton = new SynchronousExecutor();

    public static SynchronousExecutor getInstance() {
        return singleton;
    }

    @Override
    public void execute(Runnable command) {
        command.run();
    }

    private SynchronousExecutor() {
    }
}
