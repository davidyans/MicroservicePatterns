package com.bookstore.microservice.bookcatalog.dto;

import lombok.Data;

import java.util.Date;

@Data
public class BookDTO {
    private Long idBook;
    private String title;
    private String description;
    private Double price;
    private Date publicationDate;
    private String language;
    private Integer numberOfPages;
    private String isbn;
    private String publisherName;
    private Integer stockStatus;
}
