package me.wener.showea.collect.qq;

import static java.lang.ClassLoader.getSystemResourceAsStream;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import java.io.IOException;
import java.util.List;
import me.wener.showea.model.ChatMessage;
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
    public static void main(String[] args)
    {
        MessageTree.main(new String[]{"/Users/wener/gits/showea/collect/src/test/resources/qq/msg.mht"});
    }

    @Test
    public void test() throws IOException
    {
        DefaultMessageBuilder builder = new DefaultMessageBuilder();

        Message message = builder.parseMessage(getSystemResourceAsStream("qq/msg.mht"));
        Multipart multipart = (Multipart) message.getBody();
        List<Entity> parts = multipart.getBodyParts();
        String content = IOUtils.toString(((TextBody) parts.get(0).getBody()).getInputStream(), Charsets.UTF_8);

        Attachment x = MHTMessageCollector.asAttachment(parts.get(1));
        System.out.println(Hashing.md5().hashBytes(x.content()));
        System.out.println(Hashing.adler32().hashBytes(x.content()));
        System.out.println(Hashing.crc32().hashBytes(x.content()));
        System.out.println(Hashing.crc32c().hashBytes(x.content()));
        System.out.println(Hashing.sha1().hashBytes(x.content()));
        System.out.println(Hashing.sha256().hashBytes(x.content()));
        System.out.println(Hashing.sha512().hashBytes(x.content()));
        System.out.println(Hashing.murmur3_32().hashBytes(x.content()));

        System.out.println(x);
    }

    @Test
    public void testParse() throws IOException
    {
        MHTMessageCollector collector = new MHTMessageCollector();
        collector
                .host("爱抢猪食")
                .hostNumber("514403150")
                .parse(getSystemResourceAsStream("qq/msg.mht"));
        System.out.println(collector.attachments());

        for (ChatMessage message : collector.messages())
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

}
