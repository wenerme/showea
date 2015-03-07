package me.wener.showea.model;

import java.io.Serializable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface AbstractRepository<T, ID extends Serializable>
        extends PagingAndSortingRepository<T, ID>
{
}
