package me.wener.showea.collect;

import java.io.File;
import java.io.IOException;
import java.util.List;
import me.wener.showea.ShowApplication;
import me.wener.showea.collect.qq.MHTMessageCollector;
import me.wener.showea.model.FileMeta;
import me.wener.showea.model.FileMetaRepository;
import me.wener.showea.model.FileStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@PropertySource("showea.properties")
//@Import(ShowApplication.class)
@ContextConfiguration(classes = ShowApplication.class)
public class FileStoreTest
{
    @Autowired
    FileStore fileStore;
    @Autowired
    FileMetaRepository fileMetaRepo;

    @Test
    public void test() throws IOException
    {
        MHTMessageCollector.Contents contents = MHTMessageCollector
                .contents(new File("/Users/wener/gits/note/ignored/us/mht/笑笑(760355495).mht"));
        List<FileMeta> metas = fileStore.store(contents.attachments().values());
        for (FileMeta meta : metas)
        {
            System.out.println(meta);
        }
        fileMetaRepo.save(metas);
    }
}
