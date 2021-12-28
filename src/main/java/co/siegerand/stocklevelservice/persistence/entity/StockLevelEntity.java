package co.siegerand.stocklevelservice.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import co.siegerand.stocklevelservice.model.StockLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "stock_level")
@NoArgsConstructor
public class StockLevelEntity {

    @Id
    @GeneratedValue
    private int id;

    @Version
    private int version;
    private int bookId;
    private long stockLevel;

    public StockLevelEntity(int bookId, long stockLevel) {

        this.bookId = bookId;
        this.stockLevel = stockLevel;
    }

    public StockLevelEntity(StockLevel stockLevel) {
        this.bookId = stockLevel.getBookId();
        this.stockLevel = stockLevel.getStockLevel();
    }
    
}
