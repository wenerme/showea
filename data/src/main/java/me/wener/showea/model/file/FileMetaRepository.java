package me.wener.showea.model.file;

import me.wener.showea.model.AbstractRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface FileMetaRepository extends AbstractRepository<FileMeta, String>
{
}
