package me.wener.showea.collect.i518;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * i518 上的记事本内容数据模型
 */
@Data
@Accessors(chain = true, fluent = true)
@Entity
public class Note
{
    @Id
    @GeneratedValue
    private Long id;

    private Date data;
    private String content;
    private String title;
    private String weather;
}
