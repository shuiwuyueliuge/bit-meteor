package cn.mio.btm.domain.task;

import java.util.Optional;

public interface TaskRepository {

    void save(Task task);

    Optional<Task> findById(String peerId);

    void remove(String peerId);
}
