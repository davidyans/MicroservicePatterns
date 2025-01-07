package com.bookstore.microservice.bookcatalog.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 500)
    private String description;

    private Double price;

    private Date publicationDate;

    private String language;

    private Integer numberOfPages;

    private String isbn;

    private Integer publisherId;

    private Integer stockStatus = 1;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    private Integer status = 1;
}
