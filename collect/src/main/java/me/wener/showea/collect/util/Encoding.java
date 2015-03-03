package me.wener.showea.collect.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import org.apache.commons.io.IOUtils;
import org.mozilla.universalchardet.CharsetListener;
import org.mozilla.universalchardet.UniversalDetector;

public class Encoding
{
    private static final UniversalDetector detector = new UniversalDetector(null);
    private static byte[] buffer = new byte[4096];

    public static String toUTF8(byte[] bytes)
    {
        String encoding = detect(bytes);
        if (encoding == null)
        {
            throw new RuntimeException("无法检测字符编码");
        }
        return new String(bytes, Charset.forName(encoding));
    }

    public static String toUTF8(File file) throws IOException
    {
        byte[] bytes = Files.readAllBytes(file.toPath());
        return toUTF8(bytes);
    }

    public static String toUTF8(InputStream is) throws IOException
    {
        byte[] bytes = IOUtils.toByteArray(is);
        return toUTF8(bytes);
    }
    public static String toUTF8AndClose(InputStream is) throws IOException
    {
        byte[] bytes = IOUtils.toByteArray(is);
        is.close();
        return toUTF8(bytes);
    }

    public static String detect(byte[] bytes)
    {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        return detect(is);
    }

    public synchronized static String detect(InputStream is)
    {
        int nread;
        try
        {
            while ((nread = is.read(buffer)) > 0 && !detector.isDone())
            {
                detector.handleData(buffer, 0, nread);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        detector.dataEnd();

        String encoding = detector.getDetectedCharset();
        detector.reset();

        // FIXME 编码检测可能失败,即便是UTF8的也可能无法检测
        return encoding == null ? "UTF8" : encoding;
    }
}
