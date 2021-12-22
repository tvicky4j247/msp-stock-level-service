package co.siegerand.stocklevelservice;

import co.siegerand.stocklevelservice.model.StockLevel;
import co.siegerand.stocklevelservice.persistence.entity.StockLevelEntity;
import co.siegerand.stocklevelservice.persistence.repository.BookPurchaseRepository;
import co.siegerand.stocklevelservice.persistence.repository.StockLevelRepository;
import co.siegerand.stocklevelservice.persistence.repository.StockReplenishmentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

    // UTIL METHODS
    private void createBookHelper(int bookId, int stockLevel) {
        stockLevelRepository.save(new StockLevelEntity(new StockLevel(bookId, stockLevel)));
    }

}
