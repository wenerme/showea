package me.wener.showea.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * i518 上的记事本内容数据模型
 */
@Data
@Accessors(chain = true, fluent = true)
@Entity
@Table(name = "T_NOTE")
@Access(AccessType.FIELD)
public class Note implements Serializable
{
    @Id
    @GeneratedValue
    private Long id;

    private Date date;
    @Column(length = 2000)
    private String content;
    private String title;
    private String weather;


}
