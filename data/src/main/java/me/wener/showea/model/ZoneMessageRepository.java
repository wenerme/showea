package me.wener.showea.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(
        collectionResourceRel = "zone",
        path = "zone",
        itemResourceDescription = @Description("空间的故事")
)
public interface ZoneMessageRepository extends CrudRepository<ZoneMessage, Long>
{
    /**
     * Find by message id
     */
    ZoneMessage findByMid(@Param("mid") String mid);
}
