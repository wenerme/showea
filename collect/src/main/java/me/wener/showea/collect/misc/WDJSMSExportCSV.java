package me.wener.showea.collect.misc;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import me.wener.showea.collect.i518.SMS;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * 豌豆荚导出的 SMS CSV
 */
@Log
public class WDJSMSExportCSV
{
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    @SneakyThrows
    public static List<SMS> parse(String content, String hostName, String hostNumber) throws IOException
    {
        List<SMS> items = Lists.newArrayList();

        /*
sms,submit,xx,	+12345,,2014. 2.28 10:10,23,"content"
信息类型,操作类型,目标名,号码,未知,日期,未知,内容
submit 为发送
deliver 为接收
         */
        CSVParser parse = CSVParser.parse(content,
                CSVFormat.DEFAULT
                        .withHeader("msgType", "opType", "target", "targetNumber", "unknown", "date", "unknown2", "content"));

        for (CSVRecord record : parse)
        {
            if (record.size() != 8)
            {
                log.log(Level.WARNING, "Bad record " + record);
                continue;
            }

            if (!record.get("msgType").equals("sms"))
            {
                log.log(Level.WARNING, "Unexpected msgType " + record);
                continue;
            }

            switch (record.get("opType"))
            {
                case "submit":
                    break;
                case "deliver":
                    break;
                default:
                    throw new RuntimeException("Unexpected opType " + record);
            }

            String from, to, fromNumber, toNumber, target, targetNumber;
            target = record.get("target").trim();
            targetNumber = record.get("targetNumber").trim(); ;

            switch (record.get("opType"))
            {
                case "submit":
                    to = target;
                    toNumber = targetNumber;
                    from = hostName;
                    fromNumber = hostNumber;
                    break;

                case "deliver":
                    from = target;
                    fromNumber = targetNumber;
                    to = hostName;
                    toNumber = hostNumber;
                    break;
                default:
                    throw new RuntimeException("Unexpected opType " + record);
            }

            // 规范化日期格式,去除冗余空格
            String date = record.get("date");
            date = date.replaceFirst("(?<=\\.)\\s(?=\\S)", "")
                       .replaceFirst("(?<=\\S)\\s+(?=\\S)", " ");

            SMS sms = new SMS().from(from).fromNumber(fromNumber)
                               .to(to).toNumber(toNumber)
                               .data(DATE_FORMAT.parse(date))
                               .content(record.get("content"));
            items.add(sms);
        }

        return items;
    }
}
