package me.wener.showea.model;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import me.wener.showea.ShowApplication;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.web.config.EnableSpringDataWebSupport;


@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableSpringDataWebSupport
@PropertySource("showea.properties")
@Import(ShowApplication.class)
@Slf4j
public class WebMvcTest implements CommandLineRunner
{
    @Autowired
    NoteRepository noteRepository;

    public static void main(String[] args)
    {
//        SpringApplication.run(ModelTest.class,args);
        SpringApplication app = new SpringApplication(WebMvcTest.class);
        app.setShowBanner(false);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception
    {
        System.out.println("Launched");
        System.out.println("Note count " + noteRepository.count());
    }

    @Test
    public void testMapper() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        Note note = new Note().content("Yes").date(new Date()).weather("Find");
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, note);
        System.out.println(writer.toString());
    }
}
