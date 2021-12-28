package co.siegerand.stocklevelservice.persistence.entity;

import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import co.siegerand.stocklevelservice.model.StockReplenishment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@Entity
@NoArgsConstructor
@Table(name = "stock_replenishment")
public class StockReplenishmentEntity {

    @Id
    @GeneratedValue
    private int id;

    @Version
    private int version;

    private int bookId;
    private long quantityReplenished;
    private ZonedDateTime timestamp;

    private StockReplenishmentEntity(int bookId, int quantityReplenished, ZonedDateTime timeStamp) {

        id = bookId;
        this.quantityReplenished = quantityReplenished;
        this.timestamp = timeStamp;
    }

    public StockReplenishmentEntity(StockReplenishment replenishment) {
        this.bookId = replenishment.getBookId();
        this.quantityReplenished = replenishment.getQuantityReplenished();
        this.timestamp = replenishment.getTimestamp();
    }
    
}