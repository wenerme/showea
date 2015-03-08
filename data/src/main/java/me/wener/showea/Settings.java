package me.wener.showea;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Accessors(chain = true, fluent = true)
@ToString
@Component
@Configuration
public class Settings
{

    private Path fileStorePath;
    private Path rootPath;


    @Autowired
    void init(@Value("${showea.rootPath}") String rootPath,
              @Value("${showea.fileStorePath}") String fileStorePath)
    {
        fileStorePath = FilenameUtils.concat(rootPath, fileStorePath);
        rootPath = replaceHome(rootPath);
        fileStorePath = replaceHome(fileStorePath);

        this.rootPath = Paths.get(rootPath);
        this.fileStorePath = Paths.get(fileStorePath);
    }

    @PostConstruct
    void post() throws IOException
    {
        FileUtils.forceMkdir(rootPath.toFile());
        FileUtils.forceMkdir(fileStorePath.toFile());
    }

    private String replaceHome(String rootPath) {return rootPath.replaceFirst("^~", System.getProperty("user.home"));}
}
