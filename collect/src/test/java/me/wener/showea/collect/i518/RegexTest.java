package me.wener.showea.collect.i518;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import me.wener.showea.collect.util.Text;
import org.junit.Test;

public class RegexTest
{
    @Test
    public void parseNote() throws IOException
    {
        InputStream is = ClassLoader.getSystemResourceAsStream("i518/note.txt");
        String content = Text.readAndClose(is);
        System.out.println(NoteCollector.parse(content));
    }

    @Test
    public void date() throws ParseException
    {
        String d = "2011-06-15 16:20";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        System.out.println(format.format(new Date()));
        System.out.println(format.parse(d));
    }

    @Test
    public void parseSMS() throws IOException
    {
        InputStream is = ClassLoader.getSystemResourceAsStream("i518/sms.txt");
        String content = Text.readAndClose(is);
        System.out.println(SMSCollector.parse(content, "你你", null));
    }

}
