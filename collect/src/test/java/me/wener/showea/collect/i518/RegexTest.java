package me.wener.showea.collect.i518;

import java.io.IOException;
import java.io.InputStream;
import me.wener.showea.collect.util.Encoding;
import org.junit.Test;

public class RegexTest
{
    @Test
    public void parseNote() throws IOException
    {
        InputStream is = ClassLoader.getSystemResourceAsStream("i518/note.txt");
        String content = Encoding.toUTF8AndClose(is);
        System.out.println(NoteCollector.parse(content));
    }

}
