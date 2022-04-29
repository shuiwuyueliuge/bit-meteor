package cn.test;

import cn.mayu.coding.BencodingDecoder;
import cn.mayu.coding.BencodingData;
import cn.mayu.torrent.TorrentDescriptor;
import java.io.FileInputStream;
import java.util.*;

/**
 *
 */
class Test {

    @org.junit.jupiter.api.Test
    public void testBencodingSuccess() {
        // strings
        String str = "5:qwert";
        List<BencodingData> res = new BencodingDecoder(str.getBytes()).parse();
        System.out.println(res);

        // integers
        String num = "i1234e";
        List<BencodingData> nums = new BencodingDecoder(num.getBytes()).parse();
        System.out.println(nums);

        // lists
        String list = "l4:test5:abcdee";
        List<BencodingData> lists = new BencodingDecoder(list.getBytes()).parse();
        System.out.println(lists);

        // dictionaries
        String map = "d3:agei20ee";
        List<BencodingData> maps = new BencodingDecoder(map.getBytes()).parse();
        System.out.println(maps);
    }

    @org.junit.jupiter.api.Test
    public void testBencodingError() {
        // strings
        String str = "5:qwertq";
        List<BencodingData> res = new BencodingDecoder(str.getBytes()).parse();
        System.out.println(res);

        // integers
        String num = "1i1234ee";
        List<BencodingData> nums = new BencodingDecoder(num.getBytes()).parse();
        System.out.println(nums);

        // lists
        String list = "l4:test5:abcde5e";
        List<BencodingData> lists = new BencodingDecoder(list.getBytes()).parse();
        System.out.println(lists);

        // dictionaries
        String map = "d3:agei20eae";
        List<BencodingData> maps = new BencodingDecoder(map.getBytes()).parse();
        System.out.println(maps);
    }

    @org.junit.jupiter.api.Test
    public void test() throws Exception {
        String file1 = "[miobt.com][KRL字幕組][假面騎士聖刃][第32章][吾心所願、剔透玲瓏][810P].torrent";
        String file2 = "[miobt.com][Skymoon-Raws] 進擊的巨人 第四季  Shingeki no Kyojin - The Final Season - 25 [ViuTV][WEB-DL][1080p][AVC AAC][繁體外掛][MP4 ASSx2](正式版本).torrent";
        String file3 = "[miobt.com][外挂中字][剧场版 假面骑士Agito G4计划 导演剪辑版][BDRIP][1080P][官方中文].torrent";

        FileInputStream fi = new FileInputStream(file2);
        byte[] buffer = new byte[145 * 1024 * 1024];
        int i;
        byte[] b = null;
        while ((i = fi.read(buffer)) != -1) {
            b = new byte[i];
            System.arraycopy(buffer, 0, b, 0, i);
        }

        TorrentDescriptor td = TorrentDescriptor.builder().build(b);
    }
}