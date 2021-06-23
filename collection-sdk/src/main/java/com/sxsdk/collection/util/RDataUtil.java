//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sxsdk.collection.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RDataUtil {
    private static final String CHINESE_PATTERN = "[一-龥]{2,}";

    public RDataUtil() {
    }

    private static boolean match(String pattern, String str) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        boolean id = m.find();
        return id;
    }

    public static boolean isValidChinaChar(String paramString) {
        return paramString != null && match("[一-龥]{2,}", paramString);
    }


    public static String filterOffUtf8Mb4(String text) throws UnsupportedEncodingException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        int i = 0;

        while(i < bytes.length) {
            short b = (short)bytes[i];
            if (b > 0) {
                buffer.put(bytes[i++]);
            } else {
                b = (short)(b + 256);
                if ((b ^ 192) >> 4 == 0) {
                    buffer.put(bytes, i, 2);
                    i += 2;
                } else if ((b ^ 224) >> 4 == 0) {
                    buffer.put(bytes, i, 3);
                    i += 3;
                } else if ((b ^ 240) >> 4 == 0) {
                    i += 4;
                } else {
                    buffer.put(bytes[i++]);
                }
            }
        }

        buffer.flip();
        return new String(buffer.array(), StandardCharsets.UTF_8);
    }
}
