package cn.test;

import cn.mayu.bt.bencoding.BencodingData;
import cn.mayu.bt.bencoding.BencodingDecoder;
import cn.mayu.bt.torrent.TorrentBencodingDataVisitor;
import cn.mayu.bt.torrent.TorrentDescriptor;
import cn.mayu.bt.util.FileUtil;
import org.junit.jupiter.api.Test;

import java.util.*;

/**
 *
 */
class BencodingTest {

    @Test
    public void testBencodingSuccess() throws Exception {
        // strings
        String str = "5:qwert";
        List<BencodingData> res = BencodingDecoder.parse(str.getBytes());
        System.out.println(res);

        // integers
        String num = "i1234e";
        List<BencodingData> nums = BencodingDecoder.parse(num.getBytes());
        System.out.println(nums);

        // lists
        String list = "l4:test5:abcdee";
        List<BencodingData> lists = BencodingDecoder.parse(list.getBytes());
        System.out.println(lists);

        // dictionaries
        String map = "d3:agei20e4:age2i20ee";
        List<BencodingData> maps = BencodingDecoder.parse(map.getBytes());
        System.out.println(maps);
    }

    @Test
    public void testParseFile() throws Exception {
        String file1 = "[miobt.com][KRL字幕組][假面騎士聖刃][第32章][吾心所願、剔透玲瓏][810P].torrent";
        String file2 = "[miobt.com][Skymoon-Raws] 進擊的巨人 第四季  Shingeki no Kyojin - The Final Season - 25 [ViuTV][WEB-DL][1080p][AVC AAC][繁體外掛][MP4 ASSx2](正式版本).torrent";
        String file3 = "[miobt.com][外挂中字][剧场版 假面骑士Agito G4计划 导演剪辑版][BDRIP][1080P][官方中文].torrent";
        List<BencodingData> datas = BencodingDecoder.parse(FileUtil.readFromFile(file3));
        TorrentDescriptor.Builder builder = TorrentDescriptor.builder();
        TorrentBencodingDataVisitor v = new TorrentBencodingDataVisitor(builder, FileUtil.readFromFile(file3));
        datas.get(0).visit(v);
        TorrentDescriptor b = builder.build();
        System.out.println(b);
    }
}