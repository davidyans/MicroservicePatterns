package com.bookstore.microservice.cart.clients;

import com.bookstore.microservice.cart.dto.BookDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "bookcatalogClient", url = "${services.bookcatalog.url}")
public interface BookCatalogClient {

    @GetMapping("/api/books/{id}")
    BookDTO getBookById(@PathVariable("id") Integer id);
}
