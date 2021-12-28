package co.siegerand.stocklevelservice.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.time.ZonedDateTime;

import co.siegerand.stocklevelservice.persistence.entity.StockReplenishmentEntity;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class StockReplenishment {

    private final int bookId;
    private final long quantityReplenished;
    private final ZonedDateTime timestamp;

    public StockReplenishment(StockReplenishmentEntity entity) {
        this.bookId = entity.getBookId();
        this.quantityReplenished = entity.getQuantityReplenished();
        this.timestamp = entity.getTimestamp();
    }
    
}