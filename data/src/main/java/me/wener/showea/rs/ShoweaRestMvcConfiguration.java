package me.wener.showea.rs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

@Configuration
@Import(RepositoryRestMvcConfiguration.class)
public class ShoweaRestMvcConfiguration extends RepositoryRestMvcConfiguration
{

    @Override
    protected void configureJacksonObjectMapper(ObjectMapper mapper)
    {
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config)
    {
        super.configureRepositoryRestConfiguration(config);
        try
        {
            config.setBaseUri(new URI("/v"));
        } catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
    }
}
