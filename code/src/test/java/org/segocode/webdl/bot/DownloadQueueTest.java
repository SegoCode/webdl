package org.segocode.webdl.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class DownloadQueueTest {
    @Test
    void processesTasksOneAtATimeInSubmissionOrder() throws Exception {
        List<Integer> executionOrder = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger activeTasks = new AtomicInteger();
        AtomicInteger maximumActiveTasks = new AtomicInteger();
        CountDownLatch firstTaskStarted = new CountDownLatch(1);
        CountDownLatch releaseFirstTask = new CountDownLatch(1);
        CountDownLatch completed = new CountDownLatch(3);

        try (DownloadQueue queue = new DownloadQueue()) {
            queue.submit(() -> runTask(
                    1, executionOrder, activeTasks, maximumActiveTasks, firstTaskStarted, releaseFirstTask, completed));
            firstTaskStarted.await(1, TimeUnit.SECONDS);
            queue.submit(() -> runTask(2, executionOrder, activeTasks, maximumActiveTasks, null, null, completed));
            queue.submit(() -> runTask(3, executionOrder, activeTasks, maximumActiveTasks, null, null, completed));

            assertEquals(3, queue.pendingTasks());
            releaseFirstTask.countDown();
            org.junit.jupiter.api.Assertions.assertTimeoutPreemptively(Duration.ofSeconds(2), () -> completed.await());
        }

        assertEquals(List.of(1, 2, 3), executionOrder);
        assertEquals(1, maximumActiveTasks.get());
    }

    private static void runTask(
            int id,
            List<Integer> executionOrder,
            AtomicInteger activeTasks,
            AtomicInteger maximumActiveTasks,
            CountDownLatch started,
            CountDownLatch release,
            CountDownLatch completed) {
        int active = activeTasks.incrementAndGet();
        maximumActiveTasks.accumulateAndGet(active, Math::max);
        executionOrder.add(id);
        if (started != null) {
            started.countDown();
        }
        try {
            if (release != null) {
                release.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            activeTasks.decrementAndGet();
            completed.countDown();
        }
    }
}
