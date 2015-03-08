package me.wener.showea.collect.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class TextTest
{

    @Test
    public void test() throws IOException
    {
        InputStream is = ClassLoader.getSystemResourceAsStream("enc-gbk.txt");
        byte[] bytes = IOUtils.toByteArray(is);
        System.out.println(Text.toUTF8(bytes));
        assertEquals("GB18030", Text.encoding(bytes));
    }

}
