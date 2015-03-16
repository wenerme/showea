package me.wener.showea.model.file;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
@Entity
@Table(name = "T_FILE_META", indexes = {@Index(columnList = "md5")})
public class FileMeta
{
    @Id
    @Column(length = 40,
            unique = true,
            updatable = false,
            nullable = false)
    private String sha1;

    @Column(length = 32, updatable = false, nullable = false)
    private String md5;

    /**
     * File size
     */
    @Column(nullable = false, updatable = false)
    private Integer length;

    /**
     * Mime type
     */
    @Column(length = 40, nullable = false)
    private String type;
}
