package me.wener.showea.collect.qq;

import static java.lang.ClassLoader.getSystemResourceAsStream;

import com.google.common.base.Charsets;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.apache.commons.codec.net.QCodec;
import org.apache.commons.io.IOUtils;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.apache.james.mime4j.samples.tree.MessageTree;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class MHTTest
{
    @Test
    public void test() throws IOException
    {
        DefaultMessageBuilder builder = new DefaultMessageBuilder();

        Message message = builder.parseMessage(getSystemResourceAsStream("qq/msg.mht"));
        Multipart multipart = (Multipart) message.getBody();
        List<Entity> parts = multipart.getBodyParts();
        String content = IOUtils.toString(((TextBody) parts.get(0).getBody()).getInputStream(), Charsets.UTF_8);
        System.out.println(content);
        for (int i = 1; i < parts.size(); i++)
        {
            Entity entity = parts.get(i);
            System.out.println(entity.getMimeType());
        }
//        message
        QCodec codec = new QCodec(Charsets.UTF_8);
    }

    @Test
    public void testMessage() throws IOException
    {
        String content = IOUtils.toString(getSystemResourceAsStream("qq/msg.html"), Charsets.UTF_8);
        List<ChatMessage> messages = MHTMessageCollector.parseMessage(content, null, null, "爱抢猪食", "514403150");
        for (ChatMessage message : messages)
        {
            System.out.printf("%s->%s@(%3$tY-%3$tm-%3$td %3$tk:%3$tM:%3$tS): \n\t%4$s\n", message.from(), message
                    .to(), message.data(), message.content());
        }
    }

    @Test
    public void testMessage() throws IOException
    {
        String content = IOUtils.toString(getSystemResourceAsStream("qq/msg.html"), Charsets.UTF_8);
        List<ChatMessage> messages = MHTMessageCollector.parseMessage(content, null, null, "爱抢猪食", "514403150");
        for (ChatMessage message : messages)
        {
            System.out.printf("%s->%s@(%3$tY-%3$tm-%3$td %3$tk:%3$tM:%3$tS): \n\t%4$s\n", message.from(), message
                    .to(), message.data(), message.content());
        }
    }

    public static void main(String[] args)
    {
        MessageTree.main(new String[]{"/Users/wener/gits/showea/collect/src/test/resources/qq/msg.mht"});
    }
}
