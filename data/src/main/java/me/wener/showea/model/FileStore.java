package me.wener.showea.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
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
 * 主要通过 FileMeta 来访问具体的文件数据
 */
@Component
public class FileStore
{
    protected final static HashFunction sha1 = Hashing.sha1();
    protected final static HashFunction md5 = Hashing.md5();
    protected final MimeTypes mimeTypes;
    @Autowired
    protected FileMetaRepository fileMetaRepo;
    @Autowired
    protected Settings settings;

    public FileStore()
    {
        mimeTypes = MimeTypes.getDefaultMimeTypes();
    }

    public boolean exists(FileMeta meta) throws IOException
    {
        return file(meta).exists();
    }

    public File file(FileMeta meta) throws IOException
    {
        return settings.fileStorePath().resolve(filename(meta)).toFile();
    }

    public byte[] readAllBytes(FileMeta meta) throws IOException
    {
        return Files.readAllBytes(file(meta).toPath());
    }

    public FileMeta store(FileMeta meta, byte[] data) throws IOException
    {
        File file = file(meta);
        if (!file.exists())
        {
            FileUtils.writeByteArrayToFile(file, data);
        }
        return meta;
    }

    String filename(FileMeta meta) throws IOException
    {
        try
        {
            return meta.sha1() + "." + mimeTypes.forName(meta.type()).getExtension();
        } catch (MimeTypeException e)
        {
            throw new IOException(e);
        }
    }

    public List<FileMeta> store(Iterable<Attachment> attachments) throws IOException
    {
        List<FileMeta> metas = Lists.newArrayList();
        for (Attachment attachment : attachments)
        {
            metas.add(store(attachment));
        }
        return metas;
    }


    public FileMeta meta(Attachment attachment) throws IOException
    {
        Preconditions.checkNotNull(attachment.content());

        FileMeta meta = new FileMeta();
        if (attachment.type() == null)
        {
            Metadata metadata = new Metadata();
            metadata.set(Metadata.RESOURCE_NAME_KEY, attachment.name());
            MediaType mediaType = mimeTypes.detect(new ByteArrayInputStream(attachment.content()), metadata);
            attachment.type(mediaType.toString());
        }
        if (attachment.sha1() == null)
        {
            attachment.sha1(sha1.hashBytes(attachment.content()).toString());
        }

        if (attachment.md5() == null)
        {
            attachment.md5(md5.hashBytes(attachment.content()).toString());
        }

        meta.filename(attachment.name())
            .sha1(attachment.sha1())
            .md5(attachment.md5())
            .type(attachment.type())
        ;
        return meta;
    }

    public FileMeta store(Attachment attachment) throws IOException
    {
        return store(meta(attachment), attachment.content());
    }
}
