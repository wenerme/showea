package me.wener.showea.collect.qq;

import static java.lang.ClassLoader.getSystemResourceAsStream;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import jodd.jerry.Jerry;
import lombok.extern.slf4j.Slf4j;
import me.wener.showea.collect.util.Text;
import me.wener.showea.model.ChatMessage;
import me.wener.showea.model.data.Attachment;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.apache.james.mime4j.samples.tree.MessageTree;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
@Slf4j
public class MHTTest
{
    public static void main(String[] args)
    {
        MessageTree.main(new String[]{ClassLoader.getSystemResource("qq/msg.mht").getFile()});
    }

    @Test
    public void test() throws IOException, InterruptedException
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
//                .parseMHT(getSystemResourceAsStream("qq/msg.mht"))
                .collect(new FileInputStream("/Users/wener/gits/note/ignored/us/mht/笑笑(760355495).mht"))
        ;
        System.out.println(collector.attachments());

        if (false)
            for (ChatMessage message : collector.messages())
            {
                System.out.printf("%s->%s@(%3$tY-%3$tm-%3$td %3$tk:%3$tM:%3$tS): \n\t%4$s\n", message.from(), message
                        .to(), message.date(), message.content());
            }

        System.out.println(System.getProperty("user.dir"));
        Path path = Paths.get(FilenameUtils.concat(System.getProperty("user.dir"), "data/showea/images/"));
//        collector.saveAttachment(path, false);
    }


    @Test
    public void testPath() throws IOException
    {
        Path filePath = Paths.get("/path/to/your/file.jpg");
        String contentType = Files.probeContentType(filePath);
    }

    @Test
    public void dir() throws IOException
    {
        System.out.println(System.getProperty("user.dir"));
        System.out.println(System.getProperty("user.home"));


        Text.formatter().parseLocalDate("2014-1-2 23:20:10");
        String html = "<img src=\"{F482CFB7-EF27-4316-AC25-6BE51599E1B4}.dat\"><font style=\"font-size:9pt;font-family:'微软雅黑','MS Sans Serif',sans-serif;\" color=\"000000\">我晕了&nbsp;&nbsp;好丢脸<br>(本消息由您的好友通过手机QQ发送，体验手机QQ请登录：&nbsp;</font><font style=\"font-size:9pt;font-family:'微软雅黑','MS Sans Serif',sans-serif;\" color=\"000000\">http://mobile.qq.com/c</font><font style=\"font-size:9pt;font-family:'微软雅黑','MS Sans Serif',sans-serif;\" color=\"000000\">&nbsp;)&nbsp;</font>";
        ;
        Jerry $ = Jerry.jerry(html);
        Jerry $img = $.find("img");
        $img.before("![](" + $img.attr("src") + ")");
        $img.remove();
        html = $.html();
        System.out.println(html);
        String content = html.replace("<br>", "\r\n")
                             .replace("&nbsp;", " ")
                             .replaceAll("</?font[^>]*>", "");
        System.out.println(content);
        System.out.println(StringEscapeUtils.unescapeHtml4(html));
        System.out.println(Text.escapeHTML(content));
    }


    @Test
    public void testMessage() throws IOException
    {
        String content = IOUtils.toString(getSystemResourceAsStream("qq/msg.html"), Charsets.UTF_8);
        List<ChatMessage> messages = MHTMessageCollector.messages(content, null, null, "爱抢猪食", "514403150");
        for (ChatMessage message : messages)
        {
            System.out.printf("%s->%s@(%3$tY-%3$tm-%3$td %3$tk:%3$tM:%3$tS): \n\t%4$s\n", message.from(), message
                    .to(), message.date(), message.content());
        }
    }


}
