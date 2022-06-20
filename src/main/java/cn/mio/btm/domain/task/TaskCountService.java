package cn.mio.btm.domain.task;

public abstract class TaskCountService {

   public boolean addRunningTaskCount(int taskLimit) {
       int count = getRunningTaskCount();
       if (count >= taskLimit) {
           return false;
       }

       return incrRunningTask(taskLimit);
   }

    /**
     * 增加运行任务数量。
     * 多线程中需要注册并发增加的情况。
     *
     * @param taskLimit 任务上线。
     * @return true 增加成功， false增加失败。
     */
    protected abstract boolean incrRunningTask(int taskLimit);

    public abstract int getRunningTaskCount();
}
