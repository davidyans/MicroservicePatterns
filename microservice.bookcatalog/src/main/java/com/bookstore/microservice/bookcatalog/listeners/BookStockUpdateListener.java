package com.bookstore.microservice.bookcatalog.listeners;

import com.bookstore.microservice.bookcatalog.config.RabbitMQConfig;
import com.bookstore.microservice.bookcatalog.domain.Book;
import com.bookstore.microservice.bookcatalog.events.StockStatusUpdatedEvent;
import com.bookstore.microservice.bookcatalog.exceptions.BookNotFoundException;
import com.bookstore.microservice.bookcatalog.repository.BookRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookStockUpdateListener {

    @Autowired
    private BookRepository bookRepository;

    @RabbitListener(queues = RabbitMQConfig.BOOK_STOCK_UPDATE_QUEUE)
    public void handleStockStatusUpdated(StockStatusUpdatedEvent event) {
        System.out.println(">>> [BookCatalog] Received StockStatusUpdatedEvent: " + event);

        Book existingBook = bookRepository.findById(event.getBookId())
                .orElseThrow(() ->
                        new BookNotFoundException("Book not found with ID: " + event.getBookId()));

        existingBook.setStockStatus(event.getNewStockStatus());
        existingBook.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(existingBook);

        System.out.println(">>> [BookCatalog] Book ID " + event.getBookId() + " stockStatus updated to: "
                + event.getNewStockStatus());
    }
}
