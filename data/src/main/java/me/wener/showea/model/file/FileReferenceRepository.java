package me.wener.showea.model.file;

import java.util.List;
import me.wener.showea.model.AbstractRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface FileReferenceRepository extends AbstractRepository<FileReference, Long>
{
    List<FileReference> findAllBySha1(@Param("sha1") String sha1);

    FileReference findByFilenameAndSha1(@Param("filename") String fn, @Param("sha1") String sha1);
}
