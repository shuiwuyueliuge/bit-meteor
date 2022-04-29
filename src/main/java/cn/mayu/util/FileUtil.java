package cn.mayu.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件相关操作
 */
public class FileUtil {

    private static final int BUFFER = 1024;

    public static byte[] readFromFile(String filePath) throws IOException {
        try(InputStream fi = new FileInputStream(filePath)) {
            return readFromInput(fi);
        }
    }

    public static byte[] readFromInput(InputStream in) throws IOException {
        byte[] buffer = new byte[BUFFER];
        int len;
        byte[] fileData = new byte[0];
        while ((len = in.read(buffer)) != -1) {
            byte[] newData;
            if (fileData.length == 0) {
                newData = new byte[len];
            } else {
                newData = new byte[len + fileData.length];
                System.arraycopy(fileData, 0, newData, 0, fileData.length);
            }

            System.arraycopy(buffer, 0, newData, fileData.length, len);
            fileData = newData;
        }

        return fileData;
    }
}
