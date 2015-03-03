package me.wener.showea.collect.qq;

import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
@Entity
public class ZoneMessage
{
    @Id
    @GeneratedValue
    private Long id;

    private String mid;
    private String parent;
    private Date data;
    private String content;
    private Long uin;

    private List<ZoneMessage> replays = Lists.newArrayList();
}
