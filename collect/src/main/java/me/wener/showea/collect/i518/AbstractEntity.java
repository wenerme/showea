package me.wener.showea.collect.i518;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.GeneratorType;

@Entity
public class AbstractEntity
{
    @Id
    @GeneratedValue
    private String id;
}
