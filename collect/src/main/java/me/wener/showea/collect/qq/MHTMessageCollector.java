package me.wener.showea.collect.qq;

import com.google.common.collect.Lists;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import jodd.jerry.Jerry;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

@Accessors(fluent = true, chain = true)
public class MHTMessageCollector
{


    @Getter
    private final List<String> attachments = Lists.newArrayList();
    @Getter
    private String content;
    @Setter
    @Getter
    private Path attachmentPath;
    private Date date;
    private List<ChatMessage> messages = Lists.newArrayList();

    @SneakyThrows
    public static List<ChatMessage> parseMessage(String content, String target, String targetNumber, String host, String hostNumber)
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

    public void parse()
    {

    }

}
