package cn.mio.btm.infrastructure.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SegmentLock implements Lock {

    private static final Map<String, SegmentLock> LOCK_MAP = new ConcurrentHashMap<>();

    private final Lock lock;

    private SegmentLock() {
        this.lock = new ReentrantLock();
    }

    public static SegmentLock getLock(String segmentKey) {
        return LOCK_MAP.computeIfAbsent(segmentKey, k -> new SegmentLock());
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock.lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        return lock.tryLock();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return lock.tryLock(time, unit);
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    @Override
    public Condition newCondition() {
        return lock.newCondition();
    }
}
