package co.siegerand.stocklevelservice.controller;

import java.util.Optional;

import co.siegerand.stocklevelservice.persistence.entity.BookPurchaseEntity;
import co.siegerand.stocklevelservice.persistence.entity.StockLevelEntity;
import co.siegerand.stocklevelservice.persistence.entity.StockReplenishmentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import co.siegerand.stocklevelservice.exception.InvalidInputException;
import co.siegerand.stocklevelservice.model.BookPurchase;
import co.siegerand.stocklevelservice.model.StockLevel;
import co.siegerand.stocklevelservice.model.StockReplenishment;
import co.siegerand.stocklevelservice.persistence.repository.BookPurchaseRepository;
import co.siegerand.stocklevelservice.persistence.repository.StockLevelRepository;
import co.siegerand.stocklevelservice.persistence.repository.StockReplenishmentRepository;
import co.siegerand.stocklevelservice.service.StockLevelService;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@RestController
public class StockLevelServiceImpl implements StockLevelService {

    private final Logger logger = LoggerFactory.getLogger(StockLevelServiceImpl.class);
    private final StockLevelRepository stockLevelRepository;
    private final BookPurchaseRepository bookPurchaseRepository;
    private final StockReplenishmentRepository stockReplenishmentRepository;
    private final Scheduler scheduler;

    @Autowired
    public StockLevelServiceImpl(StockLevelRepository stockLevelRepository, BookPurchaseRepository bookPurchaseRepository, 
                                    StockReplenishmentRepository stockReplenishmentRepository, Scheduler scheduler) {

        this.stockLevelRepository = stockLevelRepository;
        this.bookPurchaseRepository = bookPurchaseRepository;
        this.stockReplenishmentRepository = stockReplenishmentRepository;
        this.scheduler = scheduler;
    }

    @Override
    public Mono<StockLevel> getStockLevelForBook(int bookId) {
        if (bookId < 1) throw new InvalidInputException("Book id must be positive");

        return Mono.just(getStockLevelForBookBlocking(bookId))
                .subscribeOn(scheduler);
    }

    private StockLevel getStockLevelForBookBlocking(int bookId) {
        Optional<StockLevel> optionalStockLevel = stockLevelRepository.findByBookId(bookId);
        final StockLevel stockLevel = optionalStockLevel.orElse(null);
        optionalStockLevel.orElseThrow(() -> new InvalidInputException("Invalid book id"));
        logger.info("Stock level returned for book: {}, stock level: {}", bookId, stockLevel);
        return stockLevel;
    }

    @Override
    public void purchaseBook(BookPurchase bookPurchase) {

        scheduler.schedule(() -> {
            // check book purchase is valid
            final Optional<StockLevel> optionalStockLevel = stockLevelRepository.findByBookId(bookPurchase.getBookId());
            if (optionalStockLevel.isEmpty())
                throw new InvalidInputException("Invalid book id provided: " + bookPurchase.getBookId());
            StockLevel level = optionalStockLevel.get();

            // update stock level
            level.updateStockLevel(bookPurchase);
            bookPurchaseRepository.save(new BookPurchaseEntity(bookPurchase));
            logger.info("Book with id: {} purchased successfully by user with id: {}. Quantity purchased: {}",
                    bookPurchase.getBookId(), bookPurchase.getUserId(), bookPurchase.getQuantityPurchased());
        });
    }

    @Override
    public Mono<StockLevel> replenishStock(StockReplenishment replenishment) {
        return Mono.fromCallable(() -> replenishStockBlocking(replenishment))
                .subscribeOn(scheduler);
    }

    private StockLevel replenishStockBlocking(StockReplenishment replenishment) {
        Optional<StockLevel> optionalStockLevel = stockLevelRepository.findByBookId(replenishment.getBookId());
        if (optionalStockLevel.isEmpty())
            throw new InvalidInputException("Invalid book id provided: " + replenishment.getBookId());

        final StockLevel stockLevel = optionalStockLevel.get();
        stockLevel.updateStockLevel(replenishment);
        stockLevelRepository.save(new StockLevelEntity(stockLevel));
        stockReplenishmentRepository.save(new StockReplenishmentEntity(replenishment));
        logger.info("Stock levels of book with id: {} replenished successfully with: {} books. Current stock: {}",
                replenishment.getBookId(), replenishment.getQuantityReplenished(), stockLevel.getStockLevel());
        return stockLevel;
    }

    @Override
    public Mono<StockLevel> addBookToInventory(StockLevel newBook) {
        return Mono.fromCallable(() -> addBookSynchronous(newBook))
                .subscribeOn(scheduler);
    }

    private StockLevel addBookSynchronous(StockLevel newBook) {
        // check if book id is valid
        if (stockLevelRepository.findByBookId(newBook.getBookId()).isPresent())
            throw new InvalidInputException("Book with id " + newBook.getBookId() + " already exists in inventory");

        stockLevelRepository.save(new StockLevelEntity(newBook));
        return newBook;
    }

    @Override
    public void deleteBookFromInventory(int bookId) {
        // TODO Auto-generated method stub
        
    }
    
}
