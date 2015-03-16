package me.wener.showea.model.file;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Different filename can reference to a same file meta
 */
@Data
@Accessors(chain = true, fluent = true)
@Entity
@Table(name = "T_FILE_REF", indexes = {@Index(columnList = "filename")}
)
public class FileReference
{
    @Id
    @GeneratedValue
    private Long id;

    private String filename;
    private String owner;
    private String description;
    private Date date = new Date();

    /**
     * The sha1 reference may change
     * <p/>
     * This field only used for FK
     */
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @Column(insertable = false, updatable = false)
    private String sha1;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sha1",
            referencedColumnName = "sha1",
            nullable = false)
    private FileMeta meta;


    public String md5()
    {
        return meta == null ? null : meta.md5();
    }

    public String type()
    {
        return meta == null ? null : meta.type();
    }

    public String sha1()
    {
        return meta == null ? null : meta.sha1();
    }


}
