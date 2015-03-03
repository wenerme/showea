package me.wener.showea.collect.i518;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 短信数据模型
 */
@Data
@Accessors(chain = true, fluent = true)
@Entity
public class SMS
{
    @Id
    @GeneratedValue
    private Long id;

    private Date data;
    private String content;
    private String from;
    private String to;
    private String fromNumber;
    private String toNumber;
}
