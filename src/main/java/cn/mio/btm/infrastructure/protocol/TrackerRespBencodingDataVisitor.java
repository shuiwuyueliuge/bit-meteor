package cn.mio.btm.infrastructure.protocol;

import cn.mio.btm.infrastructure.bencoding.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TrackerRespBencodingDataVisitor implements BencodingDataVisitor {

    private final TrackerHttpResponse response;

    private Number tempNum;

    private byte[] tempArr;

    public TrackerRespBencodingDataVisitor(TrackerHttpResponse response) {
        this.response = response;
    }

    @Override
    public void visitDictionary(DictionaryBencodingData dictionary) {
        BencodingData fr = dictionary.getFromDictionary("failure reason");
        if (fr != null) {
            fr.visit(this);
            response.setFailureReason(new String(tempArr));
        }

        BencodingData wm = dictionary.getFromDictionary("warning message");
        if (wm != null) {
            wm.visit(this);
            response.setWarningMessage(new String(tempArr));
        }

        BencodingData interval = dictionary.getFromDictionary("interval");
        if (interval != null) {
            interval.visit(this);
            response.setInterval(tempNum.longValue());
        }

        BencodingData mi = dictionary.getFromDictionary("min interval");
        if (mi != null) {
            mi.visit(this);
            response.setMinInterval(tempNum.longValue());
        }

        BencodingData ti = dictionary.getFromDictionary("tracker id");
        if (ti != null) {
            ti.visit(this);
            response.setTrackerId(new String(tempArr));
        }

        BencodingData complete = dictionary.getFromDictionary("complete");
        if (complete != null) {
            complete.visit(this);
            response.setComplete(tempNum.intValue());
        }

        BencodingData incomplete = dictionary.getFromDictionary("incomplete");
        if (incomplete != null) {
            incomplete.visit(this);
            response.setIncomplete(tempNum.intValue());
        }

        BencodingData compact = dictionary.getFromDictionary("compact");
        if (compact != null) {
            compact.visit(this);
            response.setComplete(tempNum.intValue());
        }

        BencodingData peers = dictionary.getFromDictionary("peers");
        if (peers != null) {
            peers.visit(this);
            byte[] peerByte = tempArr;
            if (response.getComplete() >= 1 && peerByte.length >= 6) {
                List<InetSocketAddress> list = new ArrayList<>();
                for (int a = 0; a < peerByte.length; a += 6) {
                    if (a + 6 >= peerByte.length) {
                        break;
                    }

                    String host = (peerByte[a] & 0xFF) + "."
                                + (peerByte[a + 1] & 0xFF) + "."
                                + (peerByte[a + 2] & 0xFF) + "."
                                + (peerByte[a + 3] & 0xFF);
                    int port = ((peerByte[a + 4] & 0xFF) * 256) | (peerByte[a + 5] & 0xFF);
                    list.add(new InetSocketAddress(host, port));
                }

                response.setPeers(list);
            }
        }
    }

    @Override
    public void visitNumber(NumberBencodingData number) {
        tempNum = number.getData();
    }

    @Override
    public void visitString(StringBencodingData str) {
        tempArr = str.getByteArray();
    }
}
