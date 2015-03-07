package me.wener.showea.collect.qq;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jodd.jerry.Jerry;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import me.wener.showea.model.ChatMessage;
import org.apache.commons.io.IOUtils;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.SingleBody;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

@Accessors(fluent = true, chain = true)
public class MHTMessageCollector
{
    @Getter
    private final Map<String, Attachment> attachments = Maps.newHashMap();
    @Getter
    private final List<ChatMessage> messages = Lists.newArrayList();
    private final DefaultMessageBuilder builder;
    @Getter
    @Setter
    private String target, targetNumber, host, hostNumber;

    public MHTMessageCollector() {builder = new DefaultMessageBuilder();}

    @SneakyThrows
    static List<ChatMessage> parseMessage(String content, String target, String targetNumber, String host, String hostNumber)
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");

        String message = null;
        Date date = null;
        Boolean isSend;

        for (Jerry $tr : $trs)
        {
            if ("#3568BB".equals($tr.$("td").css("color")))
            {
                // 日期
                currentDate = LocalDate.parse($tr.text().substring("日期: ".length()));
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

                    //noinspection ConstantConditions
                    date = currentDate.toDateTime(LocalTime.parse($div.text())).toDate();
                    message = $div.next().html();
                }


            }

            if (isSend == null)
            {
                continue;
            }

            ChatMessage chatMessage = new ChatMessage()
                    .content(message)
                    .data(date);
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

    public void parse(InputStream is) throws IOException
    {
        Message message = builder.parseMessage(is);
        Multipart multipart = (Multipart) message.getBody();
        List<Entity> parts = multipart.getBodyParts();
        String content = IOUtils.toString(((TextBody) parts.get(0).getBody()).getInputStream(), Charsets.UTF_8);
        parseMessage(content);

        for (int i = 1; i < parts.size(); i++)
        {
            Attachment attachment = asAttachment(parts.get(i));
            attachments.put(attachment.name(), attachment);
        }
    }

    public void parseMessage(String html)
    {
        messages.addAll(parseMessage(html, target, targetNumber, host, hostNumber));
    }

    public void clear()
    {
        attachments.clear();
        messages.clear();
    }

}
