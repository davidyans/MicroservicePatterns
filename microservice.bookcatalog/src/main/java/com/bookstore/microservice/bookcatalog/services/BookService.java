package com.bookstore.microservice.bookcatalog.services;

import com.bookstore.microservice.bookcatalog.dto.BookDTO;

import java.util.List;

public interface BookService {

    List<BookDTO> getAllBooks();
    BookDTO getBookById(Long id);
    BookDTO createBook(BookDTO bookDTO);
    BookDTO updateBook(Long id, BookDTO bookDTO);
    void deleteBook(Long id);

    void uploadBookCover(Long id, byte[] coverImage);
    byte[] getBookCover(Long id);
}
