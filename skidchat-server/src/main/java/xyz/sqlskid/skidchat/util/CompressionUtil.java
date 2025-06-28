package xyz.sqlskid.skidchat.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionUtil {

    public static byte[] compress(String textToCompress){
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bytes);
            gzipOutputStream.write(textToCompress.getBytes(StandardCharsets.UTF_8));
            gzipOutputStream.close();
            return bytes.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decompress(byte[] compressed){
        try {
            ByteArrayInputStream bytes = new ByteArrayInputStream(compressed);
            GZIPInputStream gzip = new GZIPInputStream(bytes);
            BufferedReader bf = new BufferedReader(new InputStreamReader(gzip, StandardCharsets.UTF_8));
            StringBuilder outStr = new StringBuilder();
            String line;
            while ((line=bf.readLine())!=null) {
                outStr.append(line);
            }
            return outStr.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
