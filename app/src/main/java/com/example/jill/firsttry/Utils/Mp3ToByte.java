package com.example.jill.firsttry.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *  将录下来的mp3文件转为byte数组
 */
public class Mp3ToByte {
    public static byte[] File2Bytes(File file, int byte_size) {
        byte[] b = new byte[byte_size]; //新建一个固定大小的byte数组
        try {
            FileInputStream fileInputStream = new FileInputStream(file); //input转成Stream格式
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(byte_size);
            for (int length; (length = fileInputStream.read(b)) != -1;) {
                outputStream.write(b, 0, length);
            }
            fileInputStream.close();
            outputStream.close();
            return outputStream.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
