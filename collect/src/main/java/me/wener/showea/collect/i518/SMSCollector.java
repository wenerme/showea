package me.wener.showea.collect.i518;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true, chain = true)
public class SMSCollector
{
    private String content;

    private File file;

    private final List<SMS> items = Lists.newArrayList();

    public void reset()
    {
        content = null;
        items.clear();
    }

    public void parse()
    {
    }

    public List<SMS> collect()
    {
        return ImmutableList.copyOf(items);
    }
}
