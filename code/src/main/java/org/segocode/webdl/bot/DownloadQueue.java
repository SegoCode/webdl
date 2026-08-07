package org.segocode.webdl.bot;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

final class DownloadQueue implements AutoCloseable {
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            1,
            1,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            Thread.ofVirtual().factory());

    int pendingTasks() {
        return executor.getActiveCount() + executor.getQueue().size();
    }

    void submit(Runnable task) {
        executor.submit(task);
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }
}
