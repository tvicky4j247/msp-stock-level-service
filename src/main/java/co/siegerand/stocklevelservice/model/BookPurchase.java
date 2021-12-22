package co.siegerand.stocklevelservice.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.time.ZonedDateTime;

import co.siegerand.stocklevelservice.persistence.entity.BookPurchaseEntity;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class BookPurchase {
    
    private int userId;
    private int bookId;
    private int quantityPurchased;
    private ZonedDateTime purchaseDate;

    public BookPurchase(BookPurchaseEntity entity) {
        this.userId = entity.getUserId();
        this.bookId = entity.getBookId();
        this.quantityPurchased = entity.getQuantityPurchased();
        this.purchaseDate = entity.getPurchaseDate();
    }

}
