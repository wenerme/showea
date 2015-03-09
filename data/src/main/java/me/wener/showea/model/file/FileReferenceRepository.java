package me.wener.showea.model.file;

import java.util.List;
import me.wener.showea.model.AbstractRepository;

public interface FileReferenceRepository extends AbstractRepository<FileReference, Long>
{
    List<FileReference> findAllBySha1(String sha1);
}
