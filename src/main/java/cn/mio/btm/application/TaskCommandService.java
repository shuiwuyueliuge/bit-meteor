package cn.mio.btm.application;

import cn.mio.btm.domain.PeerIdGenerator;
import cn.mio.btm.domain.task.Task;
import cn.mio.btm.domain.task.TaskCountService;
import cn.mio.btm.domain.task.TaskRepository;
import cn.mio.btm.domain.torrent.TorrentBencodingDataVisitor;
import cn.mio.btm.domain.torrent.TorrentDescriptor;
import cn.mio.btm.domain.torrent.TorrentDescriptorRepository;
import cn.mio.btm.infrastructure.bencoding.BencodingData;
import cn.mio.btm.infrastructure.bencoding.BencodingDecoder;
import cn.mio.btm.infrastructure.bencoding.BencodingParseException;
import cn.mio.btm.infrastructure.log.LogFactory;
import cn.mio.btm.infrastructure.log.Logger;
import cn.mio.btm.infrastructure.util.FileUtil;
import java.io.IOException;
import java.util.List;

public class TaskCommandService {

    private static final Logger LOG = LogFactory.getLogger(TaskCommandService.class);

    private final PeerIdGenerator idGenerator;

    private final TaskRepository taskRepository;

    private final TorrentDescriptorRepository torrentRepository;

    private final TaskCountService countService;

    private final int taskLimit;

    private static final int DEFAULT_TASK_LIMIT = 5;

    public TaskCommandService(
            PeerIdGenerator idGenerator,
            TaskRepository taskRepository,
            TorrentDescriptorRepository torrentRepository,
            TaskCountService countService
    ) {
        this(idGenerator, taskRepository, torrentRepository, countService, DEFAULT_TASK_LIMIT);
    }

    public TaskCommandService(
            PeerIdGenerator idGenerator,
            TaskRepository taskRepository,
            TorrentDescriptorRepository torrentRepository,
            TaskCountService countService, int taskLimit
    ) {
        this.idGenerator = idGenerator;
        this.taskRepository = taskRepository;
        this.torrentRepository = torrentRepository;
        this.countService = countService;
        this.taskLimit = taskLimit;
    }

    public void createTask(String filePath) throws BencodingParseException, IOException {
        LOG.info("[Task Manager] create task " + filePath);
        if (!countService.addRunningTaskCount(taskLimit)) {
            throw new IllegalStateException("[Task Manager] task wait, current running task: " + countService.getRunningTaskCount() + " task limit:" + taskLimit);
        }

        TorrentDescriptor torrentDescriptor = getTorrentDescriptor(filePath);
        torrentRepository.save(torrentDescriptor);
        String peerId = idGenerator.genAzureusStyle();
        Task task = new Task(peerId, torrentDescriptor.getIdentity());
        taskRepository.save(task);
        task.publishPeerLookupEvent();
    }

    private TorrentDescriptor getTorrentDescriptor(String filePath) throws BencodingParseException, IOException {
        byte[] fileData = FileUtil.readFromFile(filePath);
        if (fileData.length <= 0) {
            throw new IllegalArgumentException(filePath + " can read length is zero");
        }

        List<BencodingData> data = BencodingDecoder.parse(fileData);
        if (data.size() != 1) {
            throw new IllegalArgumentException(filePath + " Bencoding data not only one");
        }

        TorrentDescriptor.Builder builder = TorrentDescriptor.builder();
        TorrentBencodingDataVisitor visit = new TorrentBencodingDataVisitor(builder, fileData);
        data.get(0).visit(visit);
        return builder.build();
    }
}
