package me.wener.showea.model;

import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(
        collectionResourceRel = "chats",
        path = "chats",
        itemResourceDescription = @Description("当初你我那样说")
)
public interface ChatMessageRepository
        extends AbstractRepository<ChatMessage, Long>
{
}
