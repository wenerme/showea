package me.wener.showea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@Configuration
@PropertySource("showea.properties")
@Import(DataModuleConfiguration.class)
public class ShowApplication
{
    @Autowired
    private ApplicationContext context;
}
