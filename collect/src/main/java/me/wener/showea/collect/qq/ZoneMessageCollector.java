package me.wener.showea.collect.qq;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import lombok.SneakyThrows;
import me.wener.showea.model.ZoneMessage;

public class ZoneMessageCollector
{

    public static final JsonParser PARSER = new JsonParser();
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static int getTotal(String json)
    {
        JsonObject data = PARSER.parse(json).getAsJsonObject().getAsJsonObject("data");
        return data.get("total").getAsInt();
    }

    @SneakyThrows
    public static List<ZoneMessage> parse(String json)
    {
        List<ZoneMessage> items = Lists.newArrayList();

        JsonObject data = PARSER.parse(json).getAsJsonObject().getAsJsonObject("data");
        if (data.has("commentList"))
        {
            for (JsonElement comment : data.getAsJsonArray("commentList"))
            {
                JsonObject c = comment.getAsJsonObject();
                ZoneMessage message = new ZoneMessage()
                        .mid(c.get("id").getAsString())
                        .content(c.get("htmlContent").getAsString())
                        .uin(c.get("uin").getAsLong())
                        .data(FORMAT.parse(c.get("pubtime").getAsString()));
                for (JsonElement reply : c.getAsJsonArray("replyList"))
                {
                    JsonObject r = reply.getAsJsonObject();

                    ZoneMessage replayMessage = new ZoneMessage()
                            .content(r.get("content").getAsString())
                            .uin(r.get("uin").getAsLong())
                            .data(new Date(r.get("time").getAsLong() * 1000)) // convert to java timestamp
                    ;

                    message.replays().add(replayMessage);
                }

                items.add(message);
            }
        }
        return items;
    }

    public static List<String> getAllMessageURL(String hostUin, int num, int start, int total)
    {
        List<String> urls = Lists.newArrayList();

        while (start < total)
        {
            urls.add(getMessageURL(hostUin, num, start));
            start += num;
        }

        return urls;
    }

    public static String getMessageURL(String hostUin, int num, int start)
    {
        // "http://m.qzone.qq.com/cgi-bin/new/get_msgb?hostUin=514403150&num=10&start=20&inCharset=utf-8&outCharset=utf-8&format=json"
        String format = "http://m.qzone.qq.com/cgi-bin/new/get_msgb?hostUin=%s&num=%s&start=%s&inCharset=utf-8&outCharset=utf-8&format=json";
        return String.format(format, hostUin, num, start);
    }

    public static List<ZoneMessage> collect(String hostUin)
    {
        return null;
    }

    /**
     * 收集指定时间后的
     */
    public static List<ZoneMessage> collect(String hostUin, Date to)
    {
        return null;
    }

    /**
     * 收集指定 ID 后的
     */
    public static List<ZoneMessage> collect(String hostUin, String id)
    {
        return null;
    }


}
