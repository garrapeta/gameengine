package com.garrapeta.gameengine.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

    public static byte[] getBytesFromInputStream(InputStream is)
            throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }

    public static String getStringFromInputStream(InputStream is)
            throws IOException {
        return new String(getBytesFromInputStream(is));
    }

    public static void writeToOutputStream(OutputStream os, byte[] bytes)
            throws IOException {
        os.write(bytes, 0, bytes.length);
        os.close();
    }

    public static void writeToOutputStream(OutputStream os, String str)
            throws IOException {
        writeToOutputStream(os, str.getBytes());
    }

}
