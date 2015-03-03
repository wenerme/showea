package me.wener.showea.collect.i518;

import com.apple.eawt.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ModelTest implements CommandLineRunner
{
    @Autowired
    NoteRepository noteRepo;
    @Autowired
    SMSRepository smsRepo;

    public static void main(String[] args) {
        SpringApplication.run(ModelTest.class);
    }

    @Override
    public void run(String... strings) throws Exception
    {
        noteRepo.save(new Note().content("Content"));
        noteRepo.save(new Note().content("Content2"));

        for (Note note : noteRepo.findAll())
        {
            System.out.println(note);
        }
    }

}
