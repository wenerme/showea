package me.wener.showea.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 不同文件名可指向同样的数据信息
 */
@Data
@Accessors(chain = true, fluent = true)
@Entity
@Table(name = "T_FILE_META", indexes = {@Index(columnList = "filename")}
)
@SecondaryTable(name = "T_FILE_META_SHA",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "sha1", referencedColumnName = "sha1")
        , indexes = {@Index(columnList = "sha1", unique = true), @Index(columnList = "md5", unique = true)}
)
public class FileMeta
{
    @Id
    @GeneratedValue
    private Long id;

    private String filename;

    @Column(length = 40, table = "T_FILE_META_SHA")
    private String type;
    @Column(length = 40, table = "T_FILE_META_SHA")
    private String sha1;
    @Column(length = 32, table = "T_FILE_META_SHA")
    private String md5;
}
