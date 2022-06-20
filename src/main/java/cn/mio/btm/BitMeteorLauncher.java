package cn.mio.btm;

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
import org.apache.commons.codec.binary.Hex;

/**
 * 比特流星启动器.
 */
public class BitMeteorLauncher {

    private static final Logger LOG = LogFactory.getLogger(BitMeteorLauncher.class);

    public static void main(String[] args) throws Exception {
        String file4 = "./torrent/Shingeki no Kyojin - The Final Season.torrent";

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
                new PeerBeFoundEventConsumer(),
                new PeerLookupEventConsumer(peerService, torrentRepository, taskRepository)
        );

        LOG.info("[Bit Meteor] start...");

        // 测试开始
        taskCommandService.createTask(file4);




        String s = "13426974546f7272656e742070726f746f636f6c0000000000100005e50732bfe610ad700d1cb37cb5f709c47e6a3e9e2d7142343431302d456664484263772a4f473443";
        Hex hex = new Hex();
        byte[] b = hex.decode(s.getBytes());
        byte pstrlen = b[0];
        String pstr = new String(b, 1, 19);
        byte[] reserved = new byte[8];
        System.arraycopy(b, 20, reserved, 0, reserved.length);
        byte[] infoHash = new byte[20];
        System.arraycopy(b, 28, infoHash, 0, infoHash.length);
        String peerId = new String(b, 48, 20);
//        //-qB4410-EfdHBcw*OG4C
    }
}
