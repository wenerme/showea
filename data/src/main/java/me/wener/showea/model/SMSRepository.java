package me.wener.showea.model;


import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(
        collectionResourceRel = "sms",
        path = "sms",
        itemResourceDescription = @Description("或许我们注定在一起的")
)
public interface SMSRepository extends AbstractRepository<SMS, Long>
{

}
