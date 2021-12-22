package co.siegerand.stocklevelservice.persistence.entity;

import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import co.siegerand.stocklevelservice.model.BookPurchase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@Entity
@Table(name = "book_purchase")
@NoArgsConstructor
public class BookPurchaseEntity {
    
    @Id
    @GeneratedValue
    private int id;

    @Version
    private int version;

    private int userId;
    private int bookId;
    private int quantityPurchased;
    private ZonedDateTime purchaseDate;

    public BookPurchaseEntity(int userId, int bookId, int quantityPurchased, ZonedDateTime purchaseDate) {

        this.userId = userId;
        this.bookId = bookId;
        this.quantityPurchased = quantityPurchased;
        this.purchaseDate = purchaseDate;
    }

    public BookPurchaseEntity(BookPurchase purchase) {
        this.bookId = purchase.getBookId();
        this.userId = purchase.getUserId();
        this.quantityPurchased = purchase.getQuantityPurchased();
        this.purchaseDate = purchase.getPurchaseDate();
    }

}
