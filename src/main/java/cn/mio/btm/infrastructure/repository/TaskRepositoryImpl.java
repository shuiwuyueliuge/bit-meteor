package cn.mio.btm.infrastructure.repository;

import cn.mio.btm.domain.task.Task;
import cn.mio.btm.domain.task.TaskRepository;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TaskRepositoryImpl implements TaskRepository {

    private final Map<String, Task> tasks;

    public TaskRepositoryImpl() {
        this.tasks = new ConcurrentHashMap<>();
    }

    @Override
    public void save(Task task) {
        tasks.put(task.getPeerId(), task);
    }

    @Override
    public Optional<Task> findById(String peerId) {
        Task task = tasks.get(peerId);
        return Objects.isNull(task) ? Optional.empty() : Optional.of(task);
    }

    @Override
    public void remove(String peerId) {
        tasks.remove(peerId);
    }
}
