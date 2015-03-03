package me.wener.showea.collect.misc;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import me.wener.showea.collect.util.Encoding;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Test;

public class WDJSMSSCSVTest
{
    @Test
    public void test() throws IOException
    {
        InputStream is = ClassLoader.getSystemResourceAsStream("misc/wdj-sms.csv");
        String content = Encoding.toUTF8AndClose(is);
        System.out.println(WDJSMSExportCSV.parse(content, "你你", null));
    }

    @Test
    public void testDate() throws ParseException
    {
        String date = "2014. 2.22  7:57";
        date = date.replaceFirst("(?<=\\.)\\s(?=\\S)", "").replaceFirst("(?<=\\S)\\s+(?=\\S)", " ");
        System.out.println(date);
        System.out.println(FastDateFormat.getInstance("yyyy.MM.dd HH:mm").parse(date));
    }
}
