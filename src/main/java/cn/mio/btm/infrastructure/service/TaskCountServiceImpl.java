package cn.mio.btm.infrastructure.service;

import cn.mio.btm.domain.task.TaskCountService;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskCountServiceImpl extends TaskCountService {

    private final AtomicInteger runningTask;

    public TaskCountServiceImpl() {
        this.runningTask = new AtomicInteger(0);
    }

    @Override
    protected boolean incrRunningTask(int taskLimit) {
        runningTask.incrementAndGet();
        return true;
    }

    @Override
    public int getRunningTaskCount() {
        return runningTask.get();
    }
}
