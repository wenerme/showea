package me.wener.showea.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import me.wener.showea.util.ResultSets;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.SchemaUpdateScript;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
@PropertySource("store.properties")
@ComponentScan("me.wener.showea")
@EnableJpaRepositories("me.wener.showea")
//@EnableWebMvc
//@EnableSpringDataWebSupport
public class ModelTest implements CommandLineRunner
{
    @Autowired
    NoteRepository noteRepo;
    @Autowired
    SMSRepository smsRepo;
    @Autowired
    ZoneMessageRepository zoneMessageRepo;
    @Autowired
    ChatMessageRepository chatMessageRepo;
    @Autowired
    DataSource ds;


    public static void main(String[] args)
    {
//        SpringApplication.run(ModelTest.class,args);
        SpringApplication app = new SpringApplication(ModelTest.class);
        app.setShowBanner(false);
        app.run(args);
    }

    @PostConstruct
    public void deleteAll()
    {
        noteRepo.deleteAll();
        smsRepo.deleteAll();
        zoneMessageRepo.deleteAll();
        chatMessageRepo.deleteAll();
    }

    //    @PostConstruct
    public void process() throws SQLException
    {
        org.hibernate.cfg.Configuration cfg = new org.hibernate.cfg.Configuration();
        cfg.addAnnotatedClass(SMS.class);
        cfg.addAnnotatedClass(ChatMessage.class);
        cfg.setProperty(AvailableSettings.DIALECT, "h2");
        cfg.setProperty(AvailableSettings.HBM2DDL_AUTO, "update");
        H2Dialect dialect = new H2Dialect();
        try (Connection c = ds.getConnection())
        {
            try (Statement stmt = c.createStatement())
            {
                DatabaseMetadata metadata = new DatabaseMetadata(c, dialect, cfg);
                for (SchemaUpdateScript s : cfg.generateSchemaUpdateScriptList(dialect, metadata))
                {
                    String script = s.getScript();
                    System.out.println(script);
                    stmt.execute(script);
                }
            }
        }

    }


    @Override
    public void run(String... strings) throws Exception
    {
        {
            noteRepo.save(new Note().content("Content"));
            noteRepo.save(new Note().content("Content2"));

            for (Note note : noteRepo.findAll())
            {
                System.out.println(note);
            }
        }

        {
            ZoneMessage message = new ZoneMessage().mid("11").content("first");
            ZoneMessage second = new ZoneMessage().mid("12").content("second");
            message.replays().add(second);
            second.replays().add(new ZoneMessage().mid("13").content("third"));
            zoneMessageRepo.save(message);
            System.out.println(zoneMessageRepo.findByMid("11"));
            System.out.println(zoneMessageRepo.findByMid("12"));
            System.out.println(zoneMessageRepo.findByMid("13"));
        }

        {
            smsRepo.save(new SMS().content("hi").from("wener").to("xxxx"));
            smsRepo.save(new SMS().content("hello").from("xxx").to("wener"));
            System.out.println(smsRepo.findAll());
        }

        ResultSets.print(ds.getConnection(), "show tables");

        System.out.println("DONE");
    }

    @Configuration
    public static class JPACfg
    {
        @Bean
        public DataSource dataSource()
        {
            EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();

            return builder.setType(EmbeddedDatabaseType.H2).build();
        }


        @Bean
        public EntityManagerFactory entityManagerFactory(DataSource dataSource)
        {

            HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
            vendorAdapter.setGenerateDdl(true);

            LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
            factory.setJpaVendorAdapter(vendorAdapter);
            factory.setPackagesToScan("me.wener.showea.model");
            factory.setDataSource(dataSource);
            factory.afterPropertiesSet();
            return factory.getObject();
        }

        @Bean
        public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory)
        {
            JpaTransactionManager txManager = new JpaTransactionManager();
            txManager.setEntityManagerFactory(entityManagerFactory);
            return txManager;
        }

        @Test
        public void test()
        {
            org.hibernate.cfg.Configuration cfg = new org.hibernate.cfg.Configuration();
            cfg.addAnnotatedClass(SMS.class);
            cfg.addAnnotatedClass(ChatMessage.class);
            cfg.setProperty(AvailableSettings.DIALECT, "h2");
            cfg.setProperty(AvailableSettings.HBM2DDL_AUTO, "update");
            H2Dialect dialect = new H2Dialect();
            for (String s : cfg.generateSchemaCreationScript(dialect))
            {
                System.out.println(s);
            }
        }
    }
}
