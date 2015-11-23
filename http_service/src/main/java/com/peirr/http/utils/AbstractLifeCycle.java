/*
 * Copyright (c) 2015. Peirr, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any means is strictly prohibited.
 * Proprietary and Confidential
 */

package com.peirr.http.utils;

import android.util.Log;

import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Basic implementation of the life cycle interface for components.
 */
public abstract class AbstractLifeCycle implements LifeCycle {
    public static final String STOPPED = "STOPPED";
    public static final String FAILED = "FAILED";
    public static final String STARTING = "STARTING";
    public static final String STARTED = "STARTED";
    public static final String STOPPING = "STOPPING";
    public static final String RUNNING = "RUNNING";

    private final Object _lock = new Object();
    private final int __FAILED = -1, __STOPPED = 0, __STARTING = 1, __STARTED = 2, __STOPPING = 3;
    private volatile int _state = __STOPPED;

    protected final CopyOnWriteArrayList<LifeCycle.Listener> _listeners = new CopyOnWriteArrayList<LifeCycle.Listener>();

    protected void doStart() throws Exception {
    }

    protected void doStop() throws Exception {
    }

    public final void start() throws Exception {
        synchronized (_lock) {
            try {
                if (_state == __STARTED || _state == __STARTING)
                    return;
                setStarting();
                doStart();
                setStarted();
            } catch (Exception e) {
                setFailed(e);
                throw e;
            } catch (Error e) {
                setFailed(e);
                throw e;
            }
        }
    }

    public final void stop() throws Exception {
        synchronized (_lock) {
            try {
                if (_state == __STOPPING || _state == __STOPPED)
                    return;
                setStopping();
                doStop();
                setStopped();
            } catch (Exception e) {
                setFailed(e);
                throw e;
            } catch (Error e) {
                setFailed(e);
                throw e;
            }
        }
    }

    public boolean isRunning() {
        final int state = _state;

        return state == __STARTED || state == __STARTING;
    }

    public boolean isStarted() {
        return _state == __STARTED;
    }

    public boolean isStarting() {
        return _state == __STARTING;
    }

    public boolean isStopping() {
        return _state == __STOPPING;
    }

    public boolean isStopped() {
        return _state == __STOPPED;
    }

    public boolean isFailed() {
        return _state == __FAILED;
    }

    public void addLifeCycleListener(LifeCycle.Listener listener) {
        _listeners.add(listener);
    }

    public void removeLifeCycleListener(LifeCycle.Listener listener) {
        _listeners.remove(listener);
    }

    public String getState() {
        switch (_state) {
            case __FAILED:
                return FAILED;
            case __STARTING:
                return STARTING;
            case __STARTED:
                return STARTED;
            case __STOPPING:
                return STOPPING;
            case __STOPPED:
                return STOPPED;
        }
        return null;
    }

    public static String getState(LifeCycle lc) {
        if (lc.isStarting()) return STARTING;
        if (lc.isStarted()) return STARTED;
        if (lc.isStopping()) return STOPPING;
        if (lc.isStopped()) return STOPPED;
        return FAILED;
    }

    private void setStarted() {
        _state = __STARTED;
        Log.d(getClass().getSimpleName(), STARTED + " {}");
        for (Listener listener : _listeners)
            listener.lifeCycleStarted(this);
    }

    private void setStarting() {
        Log.d(getClass().getSimpleName(), "starting {}");
        _state = __STARTING;
        for (Listener listener : _listeners)
            listener.lifeCycleStarting(this);
    }

    private void setStopping() {
        Log.d(getClass().getSimpleName(), "stopping {}");
        _state = __STOPPING;
        for (Listener listener : _listeners)
            listener.lifeCycleStopping(this);
    }

    private void setStopped() {
        _state = __STOPPED;
        Log.d(getClass().getSimpleName(), "{} {}" + STOPPED);
        for (Listener listener : _listeners)
            listener.lifeCycleStopped(this);
    }

    private void setFailed(Throwable th) {
        _state = __FAILED;
        Log.w(getClass().getSimpleName(), FAILED + " " + this + ": " + th, th);
        for (Listener listener : _listeners)
            listener.lifeCycleFailure(this, th);
    }

    public static abstract class AbstractLifeCycleListener implements LifeCycle.Listener {
        public void lifeCycleFailure(LifeCycle event, Throwable cause) {
        }

        public void lifeCycleStarted(LifeCycle event) {
        }

        public void lifeCycleStarting(LifeCycle event) {
        }

        public void lifeCycleStopped(LifeCycle event) {
        }

        public void lifeCycleStopping(LifeCycle event) {
        }
    }
}
