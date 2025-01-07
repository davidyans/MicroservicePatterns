package com.bookstore.microservice.bookcatalog.services.implementation;

import com.bookstore.microservice.bookcatalog.domain.Book;
import com.bookstore.microservice.bookcatalog.dto.BookDTO;
import com.bookstore.microservice.bookcatalog.exceptions.BookNotFoundException;
import com.bookstore.microservice.bookcatalog.exceptions.DuplicateResourceException;
import com.bookstore.microservice.bookcatalog.mappers.BookMapper;
import com.bookstore.microservice.bookcatalog.repository.BookRepository;
import com.bookstore.microservice.bookcatalog.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(BookMapper::toBookDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
        return BookMapper.toBookDTO(book);
    }


    @Override
    public BookDTO createBook(BookDTO bookDTO) {
        if (bookDTO.getIdBook() != null && bookRepository.existsById(bookDTO.getIdBook())) {
            throw new DuplicateResourceException("Book already exists with ID: " + bookDTO.getIdBook());
        }

        Book book = BookMapper.toBookEntity(bookDTO);
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        book.setStatus(1);

        Book savedBook = bookRepository.save(book);
        return BookMapper.toBookDTO(savedBook);
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));

        existingBook.setTitle(bookDTO.getTitle());
        existingBook.setDescription(bookDTO.getDescription());
        existingBook.setPrice(bookDTO.getPrice());
        existingBook.setPublicationDate(bookDTO.getPublicationDate());
        existingBook.setLanguage(bookDTO.getLanguage());
        existingBook.setNumberOfPages(bookDTO.getNumberOfPages());
        existingBook.setIsbn(bookDTO.getIsbn());
        existingBook.setStockStatus(bookDTO.getStockStatus());
        existingBook.setUpdatedAt(LocalDateTime.now());

        Book updatedBook = bookRepository.save(existingBook);
        return BookMapper.toBookDTO(updatedBook);
    }

    @Override
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book not found with ID: " + id);
        }
        bookRepository.deleteById(id);
    }
}
