package me.wener.showea.collect.qq;

import static java.lang.ClassLoader.getSystemResourceAsStream;

import com.google.common.base.Charsets;
import java.io.IOException;
import java.util.List;
import me.wener.showea.collect.NicePrint;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ZoneMessageCollectorTest
{
    @Test
    public void test() throws IOException
    {

        List<ZoneMessage> messages = ZoneMessageCollector
                .parse(IOUtils.toString(getSystemResourceAsStream("qq/zone-msg.json"), Charsets.UTF_8));

        NicePrint.zoneMessage(messages);
    }

}
