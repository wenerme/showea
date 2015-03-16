package me.wener.showea.model.file;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import javax.validation.constraints.NotNull;
import me.wener.showea.Settings;
import me.wener.showea.model.data.Attachment;
import org.apache.commons.io.FileUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 文件存储系统
 * <p/>
 * 磁盘上的文件名为 sha1.扩展名
 * 主要通过 FileMeta 来访问具体的文件数据,FileStore的最终操作类似于 Map&lt;filename,file&gt;,
 * 文件名为唯一的标示符,遍历所有文件时,也是使用的该文件名
 */
@Component
public class FileStore
{
    protected final static HashFunction SHA1 = Hashing.sha1();
    protected final static HashFunction MD5 = Hashing.md5();
    protected final MimeTypes mimeTypes;
    @Autowired
    protected FileReferenceRepository fileRefRepo;
    @Autowired
    protected FileMetaRepository fileMetaRepo;
    @Autowired
    protected Settings settings;

    public FileStore()
    {
        mimeTypes = MimeTypes.getDefaultMimeTypes();
    }

    public File file(FileMeta meta) throws IOException
    {
        return file(filename(meta));
    }

    public File file(String filename) throws IOException
    {
        return settings.fileStorePath().resolve(filename).toFile();
    }

    public FileMeta store(FileMeta meta, byte[] data) throws IOException
    {
        File file = file(meta);
        if (!file.exists())
        {
            FileUtils.writeByteArrayToFile(file, data);
        }
        fileMetaRepo.save(meta);
        return meta;
    }


    public String filename(@NotNull FileMeta meta) throws IOException
    {
        try
        {
            return meta.sha1() + mimeTypes.forName(meta.type()).getExtension();
        } catch (MimeTypeException e)
        {
            throw new IOException(e);
        }
    }

    public FileMeta store(FileMeta meta, InputStream is) throws IOException
    {
        File file = file(meta);
        if (!file.exists())
        {
            Files.copy(is, file.toPath());
        }
        fileMetaRepo.save(meta);
        return meta;
    }

    public List<FileReference> store(Iterable<Attachment> attachments) throws IOException
    {
        List<FileReference> items = Lists.newArrayList();
        for (Attachment attachment : attachments)
        {
            items.add(store(attachment));
        }
        return items;
    }

    public FileReference store(Attachment attachment) throws IOException
    {
        FileReference ref = createReference(attachment);
        store(ref.meta(), attachment.content());
        fileRefRepo.save(ref);
        return ref;
    }

    public FileMeta createMeta(byte[] content) throws IOException
    {
        Preconditions.checkNotNull(content);
        Preconditions.checkArgument(content.length > 0);
        FileMeta meta;

        String sha1 = SHA1.hashBytes(content).toString();
        meta = fileMetaRepo.findOne(sha1);
        if (meta == null)
        {
            meta = new FileMeta();

            Metadata metadata = new Metadata();
//            metadata.set(Metadata.RESOURCE_NAME_KEY, attachment.name());
            MediaType mediaType = mimeTypes.detect(new ByteArrayInputStream(content), metadata);
            meta.type(mediaType.toString())
                .md5(MD5.hashBytes(content).toString())
                .sha1(sha1)
                .length(content.length);
        }
        return meta;
    }

    public FileReference createReference(Attachment attachment) throws IOException
    {
        FileMeta meta = createMeta(attachment);
        FileReference ref = fileRefRepo.findByFilenameAndSha1(attachment.name(), meta.sha1());
        if (ref == null)
        {
            ref = new FileReference();
            ref.meta(meta)
               .filename(attachment.name());
        }
        return ref;
    }

    public FileMeta createMeta(Attachment attachment) throws IOException
    {
        Preconditions.checkNotNull(attachment.content());
        Preconditions.checkArgument(attachment.content().length > 0);

        FileMeta meta;
        if (attachment.sha1() == null)
        {
            attachment.sha1(SHA1.hashBytes(attachment.content()).toString());
        }

        meta = fileMetaRepo.findOne(attachment.sha1());

        if (meta == null)
        {
            if (attachment.type() == null)
            {
                Metadata metadata = new Metadata();
                metadata.set(Metadata.RESOURCE_NAME_KEY, attachment.name());
                MediaType mediaType = mimeTypes.detect(new ByteArrayInputStream(attachment.content()), metadata);
                attachment.type(mediaType.toString());
            }

            if (attachment.md5() == null)
            {
                attachment.md5(MD5.hashBytes(attachment.content()).toString());
            }


            meta = new FileMeta()
                    .md5(attachment.md5())
                    .sha1(attachment.sha1())
                    .type(attachment.type())
                    .length(attachment.content().length);
        } else
        {
            if (attachment.md5() == null)
                attachment.md5(meta.md5());
            if (attachment.type() == null)
                attachment.type(meta.type());
        }

        return meta;
    }


    public byte[] readAllBytes(FileMeta meta) throws IOException
    {
        return Files.readAllBytes(file(meta).toPath());
    }

    public InputStream read(FileMeta meta) throws IOException
    {
        return new FileInputStream(file(meta));
    }
}
