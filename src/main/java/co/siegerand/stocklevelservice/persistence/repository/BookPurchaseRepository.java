package co.siegerand.stocklevelservice.persistence.repository;

import co.siegerand.stocklevelservice.persistence.entity.BookPurchaseEntity;
import reactor.core.publisher.Mono;

import org.springframework.data.repository.CrudRepository;

public interface BookPurchaseRepository extends CrudRepository<BookPurchaseEntity, Integer> {

    Mono<Void> deleteAllByBookId(int bookId);
    
}
