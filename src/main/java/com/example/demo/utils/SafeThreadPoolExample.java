package com.example.demo.utils;

import com.alibaba.fastjson2.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SafeThreadPoolExample {

    private static final ExecutorService executor = ExecutorInstance.INSTANCE.getExecutor();
    public static void main(String[] args) {


    }

    public interface Callback<T> {
        void onSuccess(T result) throws InterruptedException;
    }

    public enum ExecutorInstance {
        INSTANCE;
        private ExecutorService executor;

        int poolSize = 32;
        int queueCapacity = 1000;

        ExecutorInstance() {
            this.executor = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(queueCapacity),
                    new ThreadPoolExecutor.CallerRunsPolicy() // 当队列满时，由调用线程执行任务
            );
        }

        public ExecutorService getExecutor() {
            return executor;
        }
    }

    public static <T> List<T> doTask(List<Supplier<T>> suppliers) {

        return doTask(suppliers, null);
    }

    public static <T> List<T> doTask(List<Supplier<T>> suppliers, Callback callback) {

        List<T> results = new ArrayList<>();
        Long start = System.currentTimeMillis();
        long taskTimeoutMillis = 5000; // 每个任务的超时时间为5秒
        int numberOfTasks = 10000;

        // 创建一个具有固定大小的线程池和有限的任务队列


        try {
            List<CompletableFuture<T>> futures = suppliers.stream()
                    .map(supplier -> CompletableFuture.supplyAsync(supplier, executor)
                            .orTimeout(taskTimeoutMillis, TimeUnit.MILLISECONDS)
                            .exceptionally(ex -> {
                                System.err.println("Task 异常: " + ex.getMessage());
                                return null;
                            })).collect(Collectors.toList());

            // 等待所有任务完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // 非阻塞地处理每个任务的结果或异常
            futures.forEach(future -> {
                try {
                    T result = future.get(); // 这里不会阻塞，因为上面的 allOf 已经确保了所有任务都已完成
                    if (callback != null) {
                        callback.onSuccess(result);
                    } else {
                        results.add(result);
                    }
                } catch (Exception e) {
                    System.err.println("获取任务结果时发生异常: " + e.getCause());
                }
            });
        } finally {
            // 关闭线程池
            //executor.shutdown();
        }
        System.out.println("执行总时长: " + (System.currentTimeMillis() - start));
        return results;
    }


}