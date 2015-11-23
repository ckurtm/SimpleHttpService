/*
 * Copyright (c) 2015. Peirr, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any means is strictly prohibited.
 * Proprietary and Confidential
 */

package com.peirr.http.utils;

/* ------------------------------------------------------------ */

/**
 * ThreadPool.
 */
public interface ThreadPool {
    /* ------------------------------------------------------------ */
    public abstract boolean dispatch(Runnable job);

    /* ------------------------------------------------------------ */

    /**
     * Blocks until the thread pool is {@link LifeCycle#stop stopped}.
     */
    public void join() throws InterruptedException;

    /* ------------------------------------------------------------ */

    /**
     * @return The total number of threads currently in the pool
     */
    public int getThreads();

    /* ------------------------------------------------------------ */

    /**
     * @return The number of idle threads in the pool
     */
    public int getIdleThreads();
    
    /* ------------------------------------------------------------ */

    /**
     * @return True if the pool is low on threads
     */
    public boolean isLowOnThreads();


    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    public interface SizedThreadPool extends ThreadPool {
        public int getMinThreads();

        public int getMaxThreads();

        public void setMinThreads(int threads);

        public void setMaxThreads(int threads);
    }
}
