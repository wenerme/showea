package me.wener.showea.collect;

import java.io.File;
import java.io.IOException;
import java.util.List;
import me.wener.showea.ShowApplication;
import me.wener.showea.collect.qq.MHTMessageCollector;
import me.wener.showea.model.file.FileReference;
import me.wener.showea.model.file.FileReferenceRepository;
import me.wener.showea.model.file.FileStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//@Import(ShowApplication.class)
@ContextConfiguration(classes = ShowApplication.class)
public class FileStoreTest
{
    @Autowired
    FileStore fileStore;
    @Autowired
    FileReferenceRepository fileMetaRepo;

    @Test
    public void test() throws IOException
    {
        MHTMessageCollector.Contents contents = MHTMessageCollector
                .contents(new File("/Users/wener/gits/note/ignored/us/mht/笑笑(760355495).mht"));
        List<FileReference> metas = fileStore.store(contents.attachments().values());
        for (FileReference meta : metas)
        {
            System.out.println(meta);
        }
    }
}
