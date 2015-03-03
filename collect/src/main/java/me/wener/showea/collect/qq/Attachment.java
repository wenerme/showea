package me.wener.showea.collect.qq;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
public class Attachment
{
    private byte[] content;
    private String name;
    private String type;
}
