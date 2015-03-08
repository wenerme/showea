package me.wener.showea.collect.qq;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import jodd.jerry.Jerry;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import me.wener.showea.collect.util.Text;
import me.wener.showea.model.ChatMessage;
import org.apache.commons.io.IOUtils;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.SingleBody;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.mapdb.Atomic;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

@Accessors(fluent = true, chain = true)
@Slf4j
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

    static void parse(InputStream is, List<ChatMessage> messages, Map<String, Attachment> attachments, String target, String targetNumber, String host, String hostNumber)
            throws IOException
    {
        Message message = new DefaultMessageBuilder().parseMessage(is);
        Multipart multipart = (Multipart) message.getBody();
        List<Entity> parts = multipart.getBodyParts();
        String content = IOUtils.toString(((TextBody) parts.get(0).getBody()).getInputStream(), Charsets.UTF_8);
        messages.addAll(parseMessage(content, target, targetNumber, host, hostNumber));

        for (int i = 1; i < parts.size(); i++)
        {
            Attachment attachment = asAttachment(parts.get(i));
            attachments.put(attachment.name(), attachment);
        }
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

    public void parse(File file, boolean useCache, boolean clearCache) throws IOException
    {
        File cache = new File(file.toString() + ".cache");
        DB db = DBMaker.newFileDB(cache).make();

        try
        {
            BlockingQueue<ChatMessage> dbMsg = db.getCircularQueue("messages");
            HTreeMap<String, Attachment> dbAtt = db.getHashMap("attachments");
            HTreeMap<String, Object> dbData = db.getHashMap("data");
            Atomic.String status = db.getAtomicString("status");

            if (clearCache)
            {
                dbMsg.clear();
                dbAtt.clear();
                dbData.clear();
                status.set(null);
                log.info("Clear cache");
            }

            if (useCache && "cached".equals(status.get()))
            {
                messages.addAll(dbMsg);
                attachments.putAll(dbAtt);
            } else
            {
                if (Strings.isNullOrEmpty(status.get()))
                {
                    status.set("parse");
                }
                while (!"cached".equals(status.get()))
                {
                    switch (status.get())
                    {
                        case "parse":
                        {
                            log.info("Parsing");

                            Message message = new DefaultMessageBuilder().parseMessage(new FileInputStream(file));
                            Multipart multipart = (Multipart) message.getBody();
                            log.info("Parse complete");

                            List<Entity> parts = multipart.getBodyParts();
                            dbData.put("content", IOUtils
                                    .toString(((TextBody) parts.get(0).getBody()).getInputStream(), Charsets.UTF_8));

                            dbAtt.clear();
                            for (int i = 1; i < parts.size(); i++)
                            {
                                Attachment attachment = asAttachment(parts.get(i));
                                dbAtt.put(attachment.name(), attachment);
                            }
                            status.set("content");
                            log.info("Parse stage complete");
                        }
                        break;
                        case "content":
                        {
                            log.info("Process content");
                            dbMsg.clear();
                            List<ChatMessage> list = parseMessage((String) dbData
                                    .get("content"), target, targetNumber, host, hostNumber);
                            dbData.put("messages", list);
                            status.set("cached");
                            log.info("Everything cached");
                        }
                        break;
                    }
                }

                messages.addAll((Collection<ChatMessage>) dbData.get("messages"));
                attachments.putAll(dbAtt);
            }
        } finally
        {
            db.close();
        }

    }

    public void saveAttachment(Path path, boolean overwrite) throws IOException
    {
        for (Attachment attachment : attachments.values())
        {
            String fn = attachment.name() + attachment.getExtension();
            File file = path.resolve(fn).toFile();

            log.info("Save attachment {} -> {}", fn, file);
            attachment.save(file, overwrite);
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
