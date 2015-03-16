package me.wener.showea;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@ComponentScan("me.wener.showea.*")
@EnableJpaRepositories("me.wener.showea.*")
public class DataModuleConfiguration
{
}
