package cn.mayu.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 */
public class HttpUtil {

    public static byte[] get(String url, int connTimeout, int readTimeout) throws IOException {
        URLConnection connection = new URL(url).openConnection();
        connection.setConnectTimeout(connTimeout);
        connection.setReadTimeout(readTimeout);
        try(InputStream in = connection.getInputStream()) {
            return FileUtil.readFromInput(in);
        }
    }
}
