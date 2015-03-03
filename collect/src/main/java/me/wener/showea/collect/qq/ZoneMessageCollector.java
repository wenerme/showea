package me.wener.showea.collect.qq;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import lombok.SneakyThrows;

public class ZoneMessageCollector
{
    @SneakyThrows
    public static List<ZoneMessage> parse(String json)
    {
        List<ZoneMessage> items = Lists.newArrayList();

        JsonParser parser = new JsonParser();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        JsonObject data = parser.parse(json).getAsJsonObject().getAsJsonObject("data");
        if (data.has("commentList"))
        {
            for (JsonElement comment : data.getAsJsonArray("commentList"))
            {
                JsonObject c = comment.getAsJsonObject();
                ZoneMessage message = new ZoneMessage()
                        .mid(c.get("id").getAsString())
                        .content(c.get("htmlContent").getAsString())
                        .uin(c.get("uin").getAsLong())
                        .data(format.parse(c.get("pubtime").getAsString()));
                for (JsonElement reply : c.getAsJsonArray("replyList"))
                {
                    JsonObject r = reply.getAsJsonObject();

                    ZoneMessage replayMessage = new ZoneMessage()
                            .content(r.get("content").getAsString())
                            .uin(r.get("uin").getAsLong())
                            .data(new Date(r.get("time").getAsLong() * 1000));// convert to java timestamp
                    ;

                    message.replays().add(replayMessage);
                }

                items.add(message);
            }
        }
        return items;
    }
}
