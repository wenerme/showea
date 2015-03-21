package me.wener.showea.collect.i518;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import me.wener.showea.collect.util.Text;
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
    public static final Splitter TO_MULTI_SPLITTER = Splitter.on('、').omitEmptyStrings().trimResults();
    @Getter(AccessLevel.NONE)
    private final List<SMS> items = Lists.newArrayList();
    private String content;
    private String host, hostNumber;

    @SneakyThrows
    static List<SMS> parse(String content, String hostName, String hostNumber)
    {
        List<SMS> items = Lists.newArrayList();

        Matcher matcher = REGEX.matcher(content);
        Integer lastEnd = null;
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

            String date = matcher.group("date") + " " + matcher.group("time").replace('：', ':');

            // 在这里不处理发送给多人的情况,即 to.contains("、")
            // 因为在这里处理导致 items 比 splits 多
            SMS sms = new SMS().from(from).fromNumber(fromNumber)
                               .to(to).toNumber(toNumber)
                               .date(Text.date(date));
            items.add(sms);

        }

        String[] split = REGEX.split(content);
        Preconditions.checkState(split.length - 1 == items.size());
        // 忽略数组中的第一个元素
        for (int i = 1; i < split.length; i++)
        {
            items.get(i - 1).content(split[i].trim());
        }

        // 处理发送给多人的情况
        for (ListIterator<SMS> iterator = items.listIterator(); iterator.hasNext(); )
        {
            SMS sms = iterator.next();
            if (sms.to() == null || !sms.to().contains("、"))
            {
                continue;
            }
            List<String> targets = TO_MULTI_SPLITTER.splitToList(sms.to());
            if (targets.size() > 1)
            {
                iterator.remove();
                for (String target : targets)
                {
                    iterator.add(new SMS(sms).to(target));
                }
            }
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
