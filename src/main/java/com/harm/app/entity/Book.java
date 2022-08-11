package com.harm.app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Entity
@Data
public class Book {
    @Id @GeneratedValue
    Long id;
    String title;
    String author;
    Integer issued;
    ZonedDateTime created;
}
