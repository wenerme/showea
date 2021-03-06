package me.wener.showea.collect;

import java.io.PrintStream;
import java.util.List;
import me.wener.showea.model.ChatMessage;
import me.wener.showea.model.SMS;
import me.wener.showea.model.ZoneMessage;

public class NicePrint
{
    public static void zoneMessage(Iterable<ZoneMessage> messages)
    {
        PrintStream out = System.out;

        for (ZoneMessage message : messages)
        {
            out.printf("%s@(%2$tY-%2$tm-%2$td %2$tk:%2$tM:%2$tS):\n\t%3$s\n"
                    , message.uin(), message.data(), message.content());
            for (ZoneMessage replay : message.replays())
            {
                out.printf("\t%s@(%2$tY-%2$tm-%2$td %2$tk:%2$tM:%2$tS):\n\t\t%3$s\n"
                        , replay.uin(), replay.data(), replay.content());
            }
        }
    }

    public static void chatMessage(Iterable<ChatMessage> messages)
    {
        PrintStream out = System.out;

        for (ChatMessage message : messages)
        {
            out.printf("%s->%s@(%3$tY-%3$tm-%3$td %3$tk:%3$tM:%3$tS): \n\t%4$s\n",
                    message.from(), message.to(), message.date(), message.content());
        }
    }

    public static void sms(List<SMS> items)
    {
        PrintStream out = System.out;

        for (SMS message : items)
        {
            out.printf("%s(%s)->%s(%s)@(%5$tY-%5$tm-%5$td %5$tk:%5$tM:%5$tS): \n\t%6$s\n",
                    message.from(), message.fromNumber(),
                    message.toNumber(), message.to(),
                    message.date(), message.content());
        }
    }
}
