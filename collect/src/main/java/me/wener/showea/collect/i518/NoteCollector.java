package me.wener.showea.collect.i518;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoteCollector
{

    @SuppressWarnings("MalformedRegex")
    public static final Pattern NOTE_REGEX = Pattern.compile(
            "^(?:标题|Caption):(?<title>[^\\r\\n]*)[\\r\\n]+\n" +
                    "(?<date>[0-9-]+)\n" +
                    "\\((?<week>[^)]+)\\)\n" +
                    "(?<time>[0-9:]+)\\s*(?<weather>[^\\r\\n]+)[\\r\\n]+", Pattern.MULTILINE | Pattern.COMMENTS);

    static List<Note> parse(String content)
    {
        Matcher matcher = NOTE_REGEX.matcher(content);
        List<Note> notes = Lists.newArrayList();
        while (matcher.find())
        {
            Note note = new Note()
                    .title(matcher.group("title"))
                    .weather(matcher.group("weather"));
            notes.add(note);
        }


        String[] split = NOTE_REGEX.split(content);
        // 忽略数组中的第一个元素
        for (int i = 1; i < split.length; i++)
        {
            notes.get(i - 1).content(split[i].trim());
        }
        return notes;
    }
}
