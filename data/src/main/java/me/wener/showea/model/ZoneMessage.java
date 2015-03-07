package me.wener.showea.model;

import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * QQ空间留言
 */
@Data
@Accessors(chain = true, fluent = true)
@Entity
@Table(name = "T_ZONE_MESSAGE"
        , indexes = {@Index(columnList = "mid", unique = true)}
)
public class ZoneMessage implements Serializable
{
    /**
     * Logic ID
     */
    @Id
    @GeneratedValue
    private Long id;

    @Setter(AccessLevel.NONE)
    private Long parentId;

    /**
     * Message ID
     */
    private String mid;
    //    private String parent;
    private Date data;
    @Column(length = 1000)
    private String content;
    private Long uin;

    @OneToMany(targetEntity = ZoneMessage.class,
            orphanRemoval = true, cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    @JoinColumn(name = "parentId")
    private List<ZoneMessage> replays = Lists.newArrayList();
}
