package me.wener.showea.collect.i518;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.SneakyThrows;
import me.wener.showea.collect.util.Text;
import me.wener.showea.model.Note;

public class NoteCollector
{
    @SuppressWarnings("MalformedRegex")
    public static final Pattern REGEX = Pattern.compile(
            "^(?:标题|Caption):(?<title>[^\\r\\n]*)[\\r\\n]+\n" +
                    "(?<date>[0-9-]+)\n" +
                    "\\((?<week>[^)]+)\\)\n" +
                    "(?<time>[0-9:]+)\\s*(?<weather>[^\\r\\n]+)[\\r\\n]+", Pattern.MULTILINE | Pattern.COMMENTS);

    @SneakyThrows
    static List<Note> parse(String content)
    {
        Matcher matcher = REGEX.matcher(content);
        List<Note> items = Lists.newArrayList();
        while (matcher.find())
        {
            Note note = new Note()
                    .title(matcher.group("title"))
                    .weather(matcher.group("weather"))
                    .date(Text.date(matcher.group("date") + " " + matcher.group("time")));
            items.add(note);
        }


        String[] split = REGEX.split(content);
        // 忽略数组中的第一个元素
        for (int i = 1; i < split.length; i++)
        {
            items.get(i - 1).content(split[i].trim());
        }
        return items;
    }
}
