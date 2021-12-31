package co.siegerand.stocklevelservice.persistence.repository;

import co.siegerand.stocklevelservice.persistence.entity.BookPurchaseEntity;
import org.springframework.data.repository.CrudRepository;

public interface BookPurchaseRepository extends CrudRepository<BookPurchaseEntity, Integer> {

    void deleteAllByBookId(int bookId);
    
}
