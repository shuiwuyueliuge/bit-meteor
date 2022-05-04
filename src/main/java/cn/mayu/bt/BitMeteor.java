package cn.mayu.bt;

import cn.mayu.bt.core.*;
import cn.mayu.bt.log.LogFactory;
import cn.mayu.bt.log.Logger;
import cn.mayu.bt.protocol.AzureusPeerIdGenerator;
import cn.mayu.bt.protocol.DefaultTrackerClient;
import cn.mayu.bt.protocol.TrackerClient;
import org.apache.commons.codec.binary.Hex;

/**
 * 比特流星启动入口
 */
public class BitMeteor {

    private static final Logger LOG = LogFactory.getLogger(BitMeteor.class);

    public static void main(String[] args) throws Exception {
        String file1 = "[miobt.com][KRL字幕組][假面騎士聖刃][第32章][吾心所願、剔透玲瓏][810P].torrent";
        String file2 = "[miobt.com][Skymoon-Raws] 進擊的巨人 第四季  Shingeki no Kyojin - The Final Season - 25 [ViuTV][WEB-DL][1080p][AVC AAC][繁體外掛][MP4 ASSx2](正式版本).torrent";
        String file3 = "[miobt.com][外挂中字][剧场版 假面骑士Agito G4计划 导演剪辑版][BDRIP][1080P][官方中文].torrent";
        String file4 = "[爱恋.torrent";

        // 系统属性配置
        System.setProperty("log.level", "info");

        // 任务上限配置
        final int taskLimit = 5;

        TrackerClient trackerClient = new DefaultTrackerClient();
        PeersFinder peersFinder = new PeersFinder(trackerClient);
        TaskManager taskManager = new TaskManager(new AzureusPeerIdGenerator(), taskLimit);
        EventBus.addConsumer(
                new PeerFindEventConsumer(),
                new TaskRunningEventConsumer(peersFinder)
        );

        LOG.info("[Bit Meteor] start task limit: " + taskLimit);

        // 测试开始
        taskManager.addTask(file4);




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
