package com.harm.app.runner;

import com.harm.app.entity.Book;
import com.harm.app.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Slf4j
//@Component
public class JpaRepositoryCheckRunner implements ApplicationRunner {
    @Autowired
    BookRepository bookRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Book byTitle = bookRepository.findByTitle("hello, book");
        if(byTitle != null) {
            log.debug("found book : {}", byTitle);
        } else {
            Book newBook = new Book();
            newBook.setIssued(1);
            newBook.setTitle("hello, book");
            newBook.setAuthor("epu baal");
            newBook.setCreated(ZonedDateTime.now());
            bookRepository.save(newBook);
            log.debug("save book : {}", newBook);
        }

    }
}
