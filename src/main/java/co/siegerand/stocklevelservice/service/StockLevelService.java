package co.siegerand.stocklevelservice.service;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import co.siegerand.stocklevelservice.model.BookPurchase;
import co.siegerand.stocklevelservice.model.StockLevel;
import co.siegerand.stocklevelservice.model.StockReplenishment;
import reactor.core.publisher.Mono;

public interface StockLevelService {
    
    @GetMapping(value = "/inventory/{bookId}", produces = "application/json")
    Mono<StockLevel> getStockLevelForBook(@PathVariable int bookId);


    @PostMapping(value = "/inventory/buy", consumes = "application/json")
    Mono<StockLevel> purchaseBook(@RequestBody BookPurchase bookPurchase);


    @PostMapping(value = "/inventory/replenish", 
                    consumes = "application/json", 
                    produces = "application/json")
    Mono<StockLevel> replenishStock(@RequestBody StockReplenishment replenishment);


    @PostMapping(value = "/inventory", 
                consumes = "application/json", 
                produces = "application/json")
    Mono<StockLevel> addBookToInventory(@RequestBody StockLevel newBook);

    @DeleteMapping("/inventory/{id}")
    Mono<Void> deleteBookFromInventory(@PathVariable(value = "id") int bookId);

}
