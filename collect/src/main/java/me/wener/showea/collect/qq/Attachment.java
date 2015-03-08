package me.wener.showea.collect.qq;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.io.FileUtils;

@Data
@Accessors(chain = true, fluent = true)
@ToString(exclude = "content")
public class Attachment implements Serializable
{
    private byte[] content;
    private String name;
    private String type;

    public boolean save(File file, boolean overwrite) throws IOException
    {
        if (Files.exists(file.toPath()) && !overwrite)
        {
            return false;
        }

        FileUtils.writeByteArrayToFile(file, content);
        return true;
    }

    public String getExtension()
    {
        if (type != null && type.contains("/"))
        {
            String[] split = type.split("/");
            return "." + split[split.length - 1].toLowerCase();
        }
        return ".data";
    }
}
