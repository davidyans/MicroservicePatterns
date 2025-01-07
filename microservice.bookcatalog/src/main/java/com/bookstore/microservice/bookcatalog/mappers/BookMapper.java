package com.bookstore.microservice.bookcatalog.mappers;

import com.bookstore.microservice.bookcatalog.domain.Book;
import com.bookstore.microservice.bookcatalog.dto.BookDTO;

public class BookMapper {

    public static BookDTO toBookDTO(Book book) {
        if (book == null) {
            return null;
        }
        BookDTO dto = new BookDTO();
        dto.setIdBook(book.getId());
        dto.setTitle(book.getTitle());
        dto.setDescription(book.getDescription());
        dto.setPrice(book.getPrice());
        dto.setPublicationDate(book.getPublicationDate());
        dto.setLanguage(book.getLanguage());
        dto.setNumberOfPages(book.getNumberOfPages());
        dto.setIsbn(book.getIsbn());
        dto.setStockStatus(book.getStockStatus());
        return dto;
    }

    public static Book toBookEntity(BookDTO dto) {
        if (dto == null) {
            return null;
        }
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setDescription(dto.getDescription());
        book.setPrice(dto.getPrice());
        book.setPublicationDate(dto.getPublicationDate());
        book.setLanguage(dto.getLanguage());
        book.setNumberOfPages(dto.getNumberOfPages());
        book.setIsbn(dto.getIsbn());
        book.setStockStatus(dto.getStockStatus());
        return book;
    }
}
