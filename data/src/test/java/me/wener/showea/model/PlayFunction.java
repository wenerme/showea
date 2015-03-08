package me.wener.showea.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class PlayFunction
{
    @Test
    public void testMimeType() throws MimeTypeException, IOException
    {
        MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
        MimeType jpeg = allTypes.forName("image/jpeg");
        String jpegExt = jpeg.getExtension(); // .jpg

        TikaConfig config = TikaConfig.getDefaultConfig();
        Detector detector = config.getDetector();
//        MimeTypes allTypes = config.getMimeRepository();

        Metadata metadata = new Metadata();

//        Path path = Paths.get(FilenameUtils.concat(System.getProperty("user.dir"), "data/showea/images/"));
        Path path = Paths.get("/Users/wener/gits/note/ignored/us/msg/笑笑(760355495)_files/");
        for (File file : FileUtils.listFiles(path.toFile(), null, true))
        {
//            String type = allTypes.;
            TikaInputStream is = TikaInputStream.get(file);
            MediaType type = detector.detect(is, metadata);
            MimeType mimeType = allTypes.forName(type.toString());
            System.out.printf("FN: %s MIME: %s EXT: %s\n", file.getName(), type, mimeType.getExtension());
        }
    }

    @Test
    public void testDetect() throws IOException
    {
        TikaConfig config = TikaConfig.getDefaultConfig();
        Detector detector = config.getDetector();

//        TikaInputStream stream = TikaInputStream.get(fileOrStream);

        Metadata metadata = new Metadata();
//        metadata.add(Metadata.RESOURCE_NAME_KEY, filenameWithExtension);
//        MediaType mediaType = detector.detect(stream, metadata);
    }

    @Test
    public void testType()
    {
        FileTypeMap typeMap = MimetypesFileTypeMap.getDefaultFileTypeMap();
        Path path = Paths.get(FilenameUtils.concat(System.getProperty("user.dir"), "data/showea/images/"));
        for (File file : FileUtils.listFiles(path.toFile(), null, true))
        {
            String type = typeMap.getContentType(file);
            System.out.println(type);
        }
    }
}
