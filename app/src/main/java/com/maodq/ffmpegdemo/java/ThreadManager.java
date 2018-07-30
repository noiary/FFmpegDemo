package com.maodq.ffmpegdemo.java;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 一个简易的线程池管理类，提供三个线程池
 *
 * @author maodq
 */
public class ThreadManager {

    /** 固定线程池的线程数量 */
    private static final int FIXED_THREAD_COUNT = 5;

    /**
     * 获取一个单线程池,
     * 所有任务将会被按照加入的顺序执行，免除了同步开销的问题
     */
    public static ExecutorService getSingleExecutor() {
        return SingleExecutorService.SINGLE_EXECUTOR;
    }

    /**
     * 获取一个可缓存的线程池,
     * 如果当前线程池的规模超出了处理需求，将回收空的线程,
     * 当需求增加时，会增加线程数量,
     * 线程池规模无限制
     */
    public static ExecutorService getCachedPool() {
        return CachedExecutorService.SINGLE_POOL;
    }

    /**
     * 获取一个固定长度的线程池,
     * 当到达线程最大数量时，线程池的规模将不再变化。
     */
    public static ExecutorService getFixedPool() {
        return FixedExecutorService.SINGLE_POOL;
    }

    // single
    private static class SingleExecutorService {
        static ExecutorService SINGLE_EXECUTOR = Executors.newSingleThreadExecutor();
    }

    // 可缓存
    private static class CachedExecutorService {
        static ExecutorService SINGLE_POOL = Executors.newCachedThreadPool();
    }

    // 固定大小
    private static class FixedExecutorService {
        static ExecutorService SINGLE_POOL = Executors.newFixedThreadPool(FIXED_THREAD_COUNT);
    }
}
