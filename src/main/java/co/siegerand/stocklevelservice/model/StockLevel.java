package co.siegerand.stocklevelservice.model;

import co.siegerand.stocklevelservice.exception.InvalidInputException;
import co.siegerand.stocklevelservice.persistence.entity.StockLevelEntity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class StockLevel {

    private int bookId;
    private long stockLevel;

    public StockLevel(int bookId, long stockLevel) {
        if (stockLevel < 0)
            throw new InvalidInputException("Stock levels cannot be negative. Book id: " + bookId);

        this.bookId = bookId;
        this.stockLevel = stockLevel;
    }

    public StockLevel(StockLevelEntity entity) {
        this.bookId = entity.getBookId();
        this.stockLevel = entity.getStockLevel();
    }

    public void updateStockLevel(BookPurchase purchase) {
        if (purchase.getQuantityPurchased() > stockLevel)
            throw new InvalidInputException("Purchase quantity for " + bookId + " greater than available stock");

        stockLevel -= purchase.getQuantityPurchased();
    }

    public void updateStockLevel(StockReplenishment replenishment) {
        if (stockLevel == Long.MAX_VALUE)
            throw new InvalidInputException("Stock of " + bookId + " at maximum level and cannot be replenished");

        if (Long.MAX_VALUE - stockLevel <= replenishment.getQuantityReplenished())
            stockLevel = Long.MAX_VALUE;

        stockLevel += replenishment.getQuantityReplenished();
    }
}
