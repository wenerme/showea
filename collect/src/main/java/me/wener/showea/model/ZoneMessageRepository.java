package me.wener.showea.model;

import org.springframework.data.repository.CrudRepository;

public interface ZoneMessageRepository extends CrudRepository<ZoneMessage, Long>
{
    ZoneMessage findByMid(String mid);
}
