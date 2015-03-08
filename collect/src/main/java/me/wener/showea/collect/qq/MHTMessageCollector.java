package me.wener.showea.collect.qq;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jodd.jerry.Jerry;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import me.wener.showea.collect.util.Text;
import me.wener.showea.model.ChatMessage;
import me.wener.showea.model.data.Attachment;
import org.apache.commons.io.IOUtils;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.SingleBody;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

@Accessors(fluent = true, chain = true)
@Slf4j
public class MHTMessageCollector
{
    private static DefaultMessageBuilder BUILDER = new DefaultMessageBuilder();
    @Getter
    private final Map<String, Attachment> attachments = Maps.newHashMap();
    @Getter
    private final List<ChatMessage> messages = Lists.newArrayList();
    @Getter
    @Setter
    private String target, targetNumber, host, hostNumber;

    public MHTMessageCollector()
    {
    }

    @SneakyThrows
    public static List<ChatMessage> messages(String content, String target, String targetNumber, String host, String hostNumber)
    {
        List<ChatMessage> items = Lists.newArrayList();
        Jerry $ = Jerry.jerry(content);
        Jerry $trs = $.$("body > table > tr");

        String messageGroup = $trs.get(1).getTextContent().substring("消息分组:".length());
        if (target == null)
        {
            target = $trs.get(2).getTextContent().substring("消息对象:".length());
        }

        LocalDate currentDate = null;
        DateTimeFormatter formatter = Text.formatter();

        String message = null;
        Date date = null;
        Boolean isSend;
        for (Jerry $tr : $trs)
        {
            if ("#3568BB".equals($tr.$("td").css("color")))
            {
                // 日期
                currentDate = formatter.parseLocalDate($tr.text().substring("日期: ".length()));
                continue;
            }
            isSend = null;
            {
                Jerry $div = $tr.$("td > div:nth-child(1)");
                String color = $div.css("color");
                if ($div.size() != 0 && color != null)
                {
                    if ("#42B475".equals(color))
                    {
                        // 发送
                        isSend = true;
                    } else if ("#006EFE".equals(color))
                    {
                        // 接收
                        isSend = false;
                    } else
                    {
                        throw new RuntimeException("无法处理的节点:" + $tr.text());
                    }

                    // 可能会包含一个发送者的名字
                    $div.find("div").remove();
                    //noinspection ConstantConditions
                    date = currentDate.toDateTime(formatter.parseLocalTime($div.text())).toDate();
                    message = $div.next().html();
                }


            }

            if (isSend == null)
            {
                continue;
            }

            ChatMessage chatMessage = new ChatMessage()
                    .content(message)
                    .date(date);
            if (isSend)
            {
                chatMessage.from(host)
                           .fromNumber(hostNumber)
                           .to(target)
                           .toNumber(targetNumber)
                ;
            } else
            {
                chatMessage.from(target)
                           .fromNumber(targetNumber)
                           .to(host)
                           .toNumber(hostNumber)
                ;
            }
            items.add(chatMessage);
        }
        return items;
    }

    @SneakyThrows
    static Attachment asAttachment(Entity entity)
    {
        String name = entity.getFilename();
        if (name == null)
        {
            name = entity.getHeader().getField("Content-Location").getBody();
        }
        return new Attachment().name(name)
                               .type(entity.getMimeType())
                               .content(IOUtils.toByteArray(((SingleBody) entity.getBody()).getInputStream()));
    }

    public static Contents contents(File file) throws IOException
    {
        try (FileInputStream is = new FileInputStream(file))
        {
            return contents(is);
        }
    }

    public static Contents contents(InputStream is) throws IOException
    {
        Message message = BUILDER.parseMessage(is);
        Multipart multipart = (Multipart) message.getBody();
        List<Entity> parts = multipart.getBodyParts();
        Contents contents = new Contents();

        String content = IOUtils.toString(((TextBody) parts.get(0).getBody()).getInputStream(), Charsets.UTF_8);
        contents.content(content);
        for (int i = 1; i < parts.size(); i++)
        {
            Attachment attachment = asAttachment(parts.get(i));
            contents.attachments().put(attachment.name(), attachment);
        }
        return contents;
    }

    public void collect(InputStream is) throws IOException
    {
        Message message = BUILDER.parseMessage(is);
        Multipart multipart = (Multipart) message.getBody();
        List<Entity> parts = multipart.getBodyParts();
        String content = IOUtils.toString(((TextBody) parts.get(0).getBody()).getInputStream(), Charsets.UTF_8);
        messages(content);

        for (int i = 1; i < parts.size(); i++)
        {
            Attachment attachment = asAttachment(parts.get(i));
            attachments.put(attachment.name(), attachment);
        }
    }

    public void collect(File file) throws IOException
    {
        try (FileInputStream is = new FileInputStream(file))
        {
            collect(is);
        }
    }

    public List<ChatMessage> messages(String html)
    {
        List<ChatMessage> list = messages(html, target, targetNumber, host, hostNumber);
        messages.addAll(list);
        return list;
    }

    public void clear()
    {
        target = targetNumber = host = hostNumber = null;
        attachments.clear();
        messages.clear();
    }

    @Data
    @Accessors(fluent = true, chain = true)
    public static class Contents
    {
        private String content;
        private Map<String, Attachment> attachments = Maps.newHashMap();
    }
}
