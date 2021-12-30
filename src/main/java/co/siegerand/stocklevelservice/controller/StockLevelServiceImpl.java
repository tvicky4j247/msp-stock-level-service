package co.siegerand.stocklevelservice.controller;

import java.util.Optional;

import co.siegerand.stocklevelservice.persistence.entity.BookPurchaseEntity;
import co.siegerand.stocklevelservice.persistence.entity.StockLevelEntity;
import co.siegerand.stocklevelservice.persistence.entity.StockReplenishmentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import co.siegerand.stocklevelservice.exception.InvalidInputException;
import co.siegerand.stocklevelservice.exception.NotFoundException;
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
                .switchIfEmpty(Mono.error(() -> new NotFoundException("No book with provided id present. Id: " + bookId)))
                .subscribeOn(scheduler);
    }

    private StockLevel getStockLevelForBookBlocking(int bookId) {
        Optional<StockLevelEntity> optionalStockLevel = stockLevelRepository.findByBookId(bookId);
        final StockLevelEntity stockLevel = optionalStockLevel.orElse(null);
        optionalStockLevel.orElseThrow(() -> new NotFoundException("Invalid book id"));
        logger.info("Stock level returned for book: {}, stock level: {}", bookId, stockLevel);
        return new StockLevel(stockLevel);
    }

    @Override
    public Mono<StockLevel> purchaseBook(BookPurchase bookPurchase) {

        return Mono.just(bookPurchase)
            .map(purchase -> purchaseBookInternal(bookPurchase))
            .subscribeOn(scheduler);

    }

    @Override
    public Mono<StockLevel> replenishStock(StockReplenishment replenishment) {
        return Mono.fromCallable(() -> replenishStockBlocking(replenishment))
                .subscribeOn(scheduler);
    }

    private StockLevel replenishStockBlocking(StockReplenishment replenishment) {
        Optional<StockLevelEntity> optionalStockLevel = stockLevelRepository.findByBookId(replenishment.getBookId());
        if (optionalStockLevel.isEmpty())
            throw new InvalidInputException("Invalid book id provided: " + replenishment.getBookId());

        final StockLevelEntity stockLevel = optionalStockLevel.get();

        // check if replenishment valid
        if (validateBookReplenishment(replenishment, stockLevel)) {
            stockLevel.setStockLevel(stockLevel.getStockLevel() + replenishment.getQuantityReplenished());
        } else {
            throw new InvalidInputException(String.format("Stock replenishment failed. Max possible replenishment: %d Requested: %d", 
                                                            Long.MAX_VALUE - stockLevel.getStockLevel(), replenishment.getQuantityReplenished()));
        }

        stockLevelRepository.save(stockLevel);
        stockReplenishmentRepository.save(new StockReplenishmentEntity(replenishment));
        logger.info("Stock levels of book with id: {} replenished successfully with: {} books. Current stock: {}",
                replenishment.getBookId(), replenishment.getQuantityReplenished(), stockLevel.getStockLevel());
        return new StockLevel(stockLevel);
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

        return new StockLevel(stockLevelRepository.save(new StockLevelEntity(newBook)));
    }

    @Override
    public Mono<Void> deleteBookFromInventory(int bookId) {
        // check if book id valid
        if (bookId < 1) throw new InvalidInputException("Invalid book id provided. Id: " + bookId);

        return Mono.fromRunnable(() -> stockLevelRepository.deleteById(bookId))
            .subscribeOn(scheduler)
            .flatMap(e -> Mono.empty());
        
    }

    // UTIL METHODS
    private StockLevel purchaseBookInternal(BookPurchase bookPurchase) {
        // check book purchase is valid
        final Optional<StockLevelEntity> optionalStockLevelEntity = stockLevelRepository.findByBookId(bookPurchase.getBookId());
        if (optionalStockLevelEntity.isEmpty())
            throw new InvalidInputException("Invalid book id provided: " + bookPurchase.getBookId());
        StockLevelEntity stockLevelEntity = optionalStockLevelEntity.get();

        // validate purchase
        if (validateBookPurchase(bookPurchase, stockLevelEntity)) {
            // update stock levels
            stockLevelEntity.setStockLevel(stockLevelEntity.getStockLevel() - bookPurchase.getQuantityPurchased());
        } else {
            throw new InvalidInputException("Purchase quantity for " + bookPurchase.getBookId() + " greater than available stock");
        }

        // update entity in db
        stockLevelRepository.save(stockLevelEntity);
        bookPurchaseRepository.save(new BookPurchaseEntity(bookPurchase));
        logger.info("Book with id: {} purchased successfully by user with id: {}. Quantity purchased: {}",
                bookPurchase.getBookId(), bookPurchase.getUserId(), bookPurchase.getQuantityPurchased());
        
        return getStockLevelForBookBlocking(bookPurchase.getBookId());
    }

    private boolean validateBookPurchase(BookPurchase bookPurchase, StockLevelEntity entity) {
        return bookPurchase.getQuantityPurchased() <= entity.getStockLevel();
    }

    private boolean validateBookReplenishment(StockReplenishment replenishment, StockLevelEntity entity) {
        return replenishment.getQuantityReplenished() <= Long.MAX_VALUE - entity.getStockLevel();
    }
    
}
