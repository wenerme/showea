package me.wener.showea.collect.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.mozilla.universalchardet.UniversalDetector;

@Slf4j
public class Text
{
    private static final UniversalDetector detector = new UniversalDetector(null);
    private static final DateTimeFormatter FORMATTER = formatter0();
    private static byte[] buffer = new byte[4096];

    public static String toUTF8(byte[] bytes)
    {
        String encoding = encoding(bytes);
        if (encoding == null)
        {
            throw new RuntimeException("无法检测字符编码");
        }
        return new String(bytes, Charset.forName(encoding));
    }

    public static String read(File file) throws IOException
    {
        byte[] bytes = Files.readAllBytes(file.toPath());
        return toUTF8(bytes);
    }

    public static String read(InputStream is) throws IOException
    {
        byte[] bytes = IOUtils.toByteArray(is);
        return toUTF8(bytes);
    }

    public static String readAndClose(InputStream is) throws IOException
    {
        byte[] bytes = IOUtils.toByteArray(is);
        is.close();
        return toUTF8(bytes);
    }

    public static String encoding(byte[] bytes)
    {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        return encoding(is);
    }

    public synchronized static String encoding(InputStream is)
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
        if (encoding == null)
        {
            log.warn("编码检测失败,使用 UTF8");
            return "UTF8";
        } else return encoding;
    }

    private static DateTimeFormatter formatter0()
    {
        DateTimeParser[] parsers = {
                DateTimeFormat.forPattern("yyyy/MM/dd").getParser()
                , DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss").getParser()
                , DateTimeFormat.forPattern("yyyy-MM-dd").getParser()
                , DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").getParser()
                , DateTimeFormat.forPattern("HH:mm:ss").getParser()
        };
        return new DateTimeFormatterBuilder().append(null, parsers).toFormatter();
    }

    public static DateTimeFormatter formatter()
    {
        return FORMATTER;
    }
}
