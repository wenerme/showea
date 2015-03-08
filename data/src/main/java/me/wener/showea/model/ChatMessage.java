package me.wener.showea.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 聊天数据模型
 */
@Data
@Accessors(chain = true, fluent = true)
@Entity
@Table(name = "T_CHAT_MESSAGE")
public class ChatMessage implements Serializable
{
    @Id
    @GeneratedValue
    private Long id;

    private Date date;
    @Column(length = 4000)
    private String content;
    @Column(name = "fromName", length = 20)
    private String from;
    @Column(name = "toName", length = 20)
    private String to;
    @Column(length = 20)
    private String fromNumber;
    @Column(length = 20)
    private String toNumber;
}
