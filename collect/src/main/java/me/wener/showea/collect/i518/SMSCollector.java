package me.wener.showea.collect.i518;

import com.google.common.collect.Lists;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import me.wener.showea.model.SMS;

@Getter
@Setter
@Accessors(fluent = true, chain = true)
public class SMSCollector
{
    @SuppressWarnings("MalformedRegex")
    public static final Pattern REGEX = Pattern.compile("^\n" +
            "(?<type>收件人|发件人|To|From)[：:]\n" +
            "(?<target>[^\\r\\n]*)[\\r\\n]+\n" +
            "(?<date>[^\\s]+)\\s+(?<time>[^\\r\\n]*)[\\r\\n]", Pattern.COMMENTS | Pattern.MULTILINE);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    @Getter(AccessLevel.NONE)
    private final List<SMS> items = Lists.newArrayList();
    private String content;
    private String host, hostNumber;

    @SneakyThrows
    static List<SMS> parse(String content, String hostName, String hostNumber)
    {
        List<SMS> items = Lists.newArrayList();

        Matcher matcher = REGEX.matcher(content);
        while (matcher.find())
        {
            String from, to, fromNumber, toNumber, target, targetNumber;
            target = matcher.group("target").trim();
            targetNumber = null;
            if (isNumber(target))
            {
                targetNumber = target;
                target = null;
            }

            switch (matcher.group("type"))
            {
                case "收件人":
                case "To":
                    to = target;
                    toNumber = targetNumber;
                    from = hostName;
                    fromNumber = hostNumber;
                    break;

                case "发件人":
                case "From":
                    from = target;
                    fromNumber = targetNumber;
                    to = hostName;
                    toNumber = hostNumber;
                    break;
                default:
                    throw new RuntimeException("解析错误: " + matcher.group());
            }

            if (to != null && to.contains("、"))
            {
                String[] split = to.split("、");
                for (String s : split)
                {
                    to = s;

                    SMS sms = new SMS().from(from).fromNumber(fromNumber)
                                       .to(to).toNumber(toNumber)
                                       .data(DATE_FORMAT.parse(matcher.group("date") + " " + matcher.group("time")
                                                                                                    .replace('：', ':')));
                    items.add(sms);
                }
            } else
            {
                SMS sms = new SMS().from(from).fromNumber(fromNumber)
                                   .to(to).toNumber(toNumber)
                                   .data(DATE_FORMAT.parse(matcher.group("date") + " " + matcher.group("time")
                                                                                                .replace('：', ':')));
                items.add(sms);
            }

        }


        String[] split = REGEX.split(content);
        // 忽略数组中的第一个元素
        for (int i = 1; i < split.length; i++)
        {
            items.get(i - 1).content(split[i].trim());
        }
        return items;
    }

    private static boolean isNumber(String str)
    {
        return str.matches("^\\+?[-\\d]+$");
    }

    public static void main(String[] args)
    {
        if (isNumber("+8613550814520"))
        {
            System.out.println("Match");
        }
    }

    public void reset()
    {
        content = null;
        items.clear();
    }

    public void parse()
    {
        items.addAll(parse(content, host, hostNumber));
    }

    public List<SMS> collect()
    {
        return items;
    }
}
