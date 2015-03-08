package me.wener.showea.model.data;

import java.io.Serializable;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
@ToString(exclude = "content")
public class Attachment implements Serializable
{
    private byte[] content;
    private String name;
    private String type;
    private String sha1;
    private String md5;
}
