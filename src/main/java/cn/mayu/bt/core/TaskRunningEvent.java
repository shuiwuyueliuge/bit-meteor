package cn.mayu.bt.core;

/**
 *
 */
public class TaskRunningEvent implements EventBus.Event {

    private final Task task;

    public TaskRunningEvent(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
