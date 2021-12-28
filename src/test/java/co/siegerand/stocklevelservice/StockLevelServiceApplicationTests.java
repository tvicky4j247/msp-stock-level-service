package co.siegerand.stocklevelservice;

import co.siegerand.stocklevelservice.model.BookPurchase;
import co.siegerand.stocklevelservice.model.StockLevel;
import co.siegerand.stocklevelservice.model.StockReplenishment;
import co.siegerand.stocklevelservice.persistence.entity.StockLevelEntity;
import co.siegerand.stocklevelservice.persistence.repository.BookPurchaseRepository;
import co.siegerand.stocklevelservice.persistence.repository.StockLevelRepository;
import co.siegerand.stocklevelservice.persistence.repository.StockReplenishmentRepository;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                properties = {"eureka.client.enabled=false"})
class StockLevelServiceApplicationTests extends MySqlDbContainer {

    private final BookPurchaseRepository bookPurchaseRepository;
    private final StockReplenishmentRepository replenishmentRepository;
    private final StockLevelRepository stockLevelRepository;
    private final WebTestClient webTestClient;

    @Autowired
    public StockLevelServiceApplicationTests(BookPurchaseRepository bookPurchaseRepository,
                                             StockReplenishmentRepository replenishmentRepository,
                                             StockLevelRepository stockLevelRepository,
                                             WebTestClient webTestClient) {

        this.bookPurchaseRepository = bookPurchaseRepository;
        this.replenishmentRepository = replenishmentRepository;
        this.stockLevelRepository = stockLevelRepository;
        this.webTestClient = webTestClient;
    }

    @BeforeEach
    void clearDatabaseData() {
        bookPurchaseRepository.deleteAll();
        replenishmentRepository.deleteAll();
        stockLevelRepository.deleteAll();
    }

    @Test
    void getStockLevelForBook() {
        int bookId = 1;

        // create book
        createBookHelper(bookId, 2);

        // get book
        final StockLevel responseBody = webTestClient.get()
                .uri("/inventory/" + bookId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockLevel.class).returnResult().getResponseBody();

        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals(2, responseBody.getStockLevel());
        Assertions.assertEquals(bookId, responseBody.getBookId());
    }

    @Test
    void addBookToInventoryAndCheckItWasAddedSuccessfully() {
        int bookId = 1;
        int stockLevel = 2;

        // create book
        final StockLevel responseBody = webTestClient.post()
                .uri("/inventory")
                .body(Mono.just(new StockLevel(bookId, stockLevel)), StockLevel.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockLevel.class).returnResult().getResponseBody();

        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals(responseBody.getBookId(), bookId);
        Assertions.assertEquals(responseBody.getStockLevel(), stockLevel);
    }

    @Test
    void purchaseBook() {
        int bookId = 1;
        int stockLevel = 10;
        int booksPurchased = 5;
        
        // create book
        createBookHelper(bookId, stockLevel);

        // purchase book via api
        BookPurchase bookPurchase = new BookPurchase(1, bookId, booksPurchased, ZonedDateTime.now());
        
        StockLevel sLevel = webTestClient.post()
            .uri("/inventory/buy")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(bookPurchase)
            .exchange()
            .expectStatus().isOk()
            .expectBody(StockLevel.class).returnResult().getResponseBody();
    
        Assertions.assertNotNull(sLevel);
        Assertions.assertEquals(stockLevel - booksPurchased, sLevel.getStockLevel());
    }

    @Test
    void purchaseBookOutOfStock() {
        int bookId = 1;
        int stockLevel = 3;
        int booksPurchased = 5;
        
        // create book
        createBookHelper(bookId, stockLevel);

        // purchase book via api
        BookPurchase bookPurchase = new BookPurchase(1, bookId, booksPurchased, ZonedDateTime.now());

        webTestClient.post()
            .uri("/inventory/buy")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(bookPurchase)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

    }

    @Test
    void replenishStock() {
        int bookId = 1;
        int initialStock = 10;
        int replenish = 10;

        // create book
        createBookHelper(bookId, initialStock);

        // replenish book supply
        StockReplenishment replenishment = new StockReplenishment(bookId, replenish, ZonedDateTime.now());

        StockLevel newStockLevel = webTestClient.post()
            .uri("/inventory/replenish")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(replenishment)
            .exchange()
            .expectStatus().isOk()
            .expectBody(StockLevel.class).returnResult().getResponseBody();
        
        Assertions.assertNotNull(newStockLevel);
        Assertions.assertEquals(initialStock + replenish, newStockLevel.getStockLevel());
    }

    @Test
    void replenishStockFail() {
        int bookId = 1;
        int initialStock = 300;
        long replenish = Long.MAX_VALUE;

        // create book
        createBookHelper(bookId, initialStock);

        // replenish stock - expect failure
        StockReplenishment replenishment = new StockReplenishment(bookId, replenish, ZonedDateTime.now());

        webTestClient.post()
            .uri("/inventory/replenish")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(replenishment)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void deleteBook() {
        int bookId = 1;
        // create book
        createBookHelper(bookId, 10);
        Assertions.assertTrue(stockLevelRepository.findByBookId(bookId).isPresent());

        // delete book
        webTestClient.delete()
            .uri("/inventory/" + bookId)
            .exchange()
            .expectStatus().isOk();
    }

    // UTIL METHODS
    private void createBookHelper(int bookId, int stockLevel) {
        stockLevelRepository.save(new StockLevelEntity(new StockLevel(bookId, stockLevel)));
    }

}
