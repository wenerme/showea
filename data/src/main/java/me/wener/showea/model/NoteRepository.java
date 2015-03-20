package me.wener.showea.model;

import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(
        collectionResourceRel = "note",
        path = "note",
        itemResourceDescription = @Description("那些事,还记得么")
)
public interface NoteRepository extends AbstractRepository<Note, Long>
{

}
