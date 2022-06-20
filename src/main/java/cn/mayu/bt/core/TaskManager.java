//package cn.mayu.bt.core;
//
//import cn.mayu.bt.bencoding.BencodingData;
//import cn.mayu.bt.bencoding.BencodingDecoder;
//import cn.mayu.bt.bencoding.BencodingParseException;
//import cn.mayu.bt.log.LogFactory;
//import cn.mayu.bt.log.Logger;
//import cn.mayu.bt.protocol.*;
//import cn.mayu.bt.torrent.TorrentBencodingDataVisitor;
//import cn.mayu.bt.torrent.TorrentDescriptor;
//import cn.mio.btm.infrastructure.util.FileUtil;
//import java.io.IOException;
//import java.util.*;
//import java.util.concurrent.*;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * bt下载任务管理
// */
//public class TaskManager {
//
//    private static final Logger LOG = LogFactory.getLogger(TaskManager.class);
//
//    private final Map<String, Task> taskCache;
//
//    private final PeerIdGenerator idGenerator;
//
//    private final int taskLimit;
//
//    private final AtomicInteger runningTask;
//
//    public TaskManager(PeerIdGenerator idGenerator, int taskLimit) {
//        this.idGenerator = idGenerator;
//        this.taskLimit = taskLimit;
//        this.taskCache = new ConcurrentHashMap<>();
//        this.runningTask = new AtomicInteger(0);
//    }
//
//    public void addTask(String filePath) throws IOException, BencodingParseException {
//        LOG.info("[Task Manager] create task " + filePath);
//        TorrentDescriptor torrentDescriptor = getTorrentDescriptor(filePath);
//        String peerId = idGenerator.genPeerId();
//        Task task = new Task(peerId, torrentDescriptor);
//        taskCache.put(peerId, task);
//        if (runningTask.get() >= taskLimit) {
//            LOG.info("[Task Manager] task wait, running task: " + runningTask.get() + " task limit:" + taskLimit);
//            return;
//        }
//
//        runningTask.incrementAndGet();
//        LOG.info("[Task Manager] task running, running task: " + runningTask.get() + " task limit:" + taskLimit);
//        EventBus.publish(new TaskRunningEvent(task));
//    }
//
//    private TorrentDescriptor getTorrentDescriptor(String filePath) throws BencodingParseException, IOException {
//        byte[] fileData = FileUtil.readFromFile(filePath);
//        if (fileData.length <= 0) {
//            throw new IllegalArgumentException(filePath + " can read length is zero");
//        }
//
//        List<BencodingData> data = BencodingDecoder.parse(fileData);
//        if (data.size() != 1) {
//            throw new IllegalArgumentException(filePath + " Bencoding data not only one");
//        }
//
//        TorrentDescriptor.Builder builder = TorrentDescriptor.builder();
//        TorrentBencodingDataVisitor visit = new TorrentBencodingDataVisitor(builder, fileData);
//        data.get(0).visit(visit);
//        return builder.build();
//    }
//}
