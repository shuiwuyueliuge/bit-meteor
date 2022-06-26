package cn.mio.btm;

import cn.mio.btm.application.PeerActiveEventConsumer;
import cn.mio.btm.application.PeerBeFoundEventConsumer;
import cn.mio.btm.application.PeerLookupEventConsumer;
import cn.mio.btm.application.TaskCommandService;
import cn.mio.btm.domain.EventBus;
import cn.mio.btm.domain.PeerIdGenerator;
import cn.mio.btm.domain.task.PeerService;
import cn.mio.btm.domain.task.TaskCountService;
import cn.mio.btm.domain.task.TaskRepository;
import cn.mio.btm.domain.torrent.TorrentDescriptorRepository;
import cn.mio.btm.infrastructure.log.LogFactory;
import cn.mio.btm.infrastructure.log.Logger;
import cn.mio.btm.infrastructure.protocol.DefaultTrackerClient;
import cn.mio.btm.infrastructure.protocol.TrackerClient;
import cn.mio.btm.infrastructure.repository.TaskRepositoryImpl;
import cn.mio.btm.infrastructure.repository.TorrentDescriptorRepositoryImpl;
import cn.mio.btm.infrastructure.service.PeerIdGeneratorImpl;
import cn.mio.btm.infrastructure.service.PeerServiceImpl;
import cn.mio.btm.infrastructure.service.TaskCountServiceImpl;

/**
 * 比特流星启动器.
 */
public class BitMeteorLauncher {

    private static final Logger LOG = LogFactory.getLogger(BitMeteorLauncher.class);

    public static void main(String[] args) throws Exception {
        String file4 = "./torrent/女性向遊戲世界對路人角色很不友好.torrent";
        String file3 = "./torrent/假面骑士Agito G4计划.torrent";

        // 系统属性配置
        System.setProperty("log.level", "info");

        TrackerClient trackerClient = new DefaultTrackerClient();
        PeerService peerService = new PeerServiceImpl(trackerClient);
        TorrentDescriptorRepository torrentRepository = new TorrentDescriptorRepositoryImpl();
        TaskRepository taskRepository = new TaskRepositoryImpl();
        PeerIdGenerator idGenerator = new PeerIdGeneratorImpl();
        TaskCountService countService = new TaskCountServiceImpl();
        TaskCommandService taskCommandService = new TaskCommandService(
                idGenerator, taskRepository, torrentRepository, countService
        );

        EventBus.addConsumer(
                new PeerActiveEventConsumer(taskRepository, torrentRepository),
                new PeerBeFoundEventConsumer(taskRepository),
                new PeerLookupEventConsumer(peerService, torrentRepository, taskRepository)
        );

        LOG.info("[Bit Meteor] start...");

        // 测试开始
        taskCommandService.createTask(file4);
//        taskCommandService.createTask(file3);
    }
}
