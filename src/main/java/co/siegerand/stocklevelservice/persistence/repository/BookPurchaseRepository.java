package co.siegerand.stocklevelservice.persistence.repository;

import co.siegerand.stocklevelservice.persistence.entity.BookPurchaseEntity;
import org.springframework.data.repository.CrudRepository;

import co.siegerand.stocklevelservice.model.BookPurchase;

public interface BookPurchaseRepository extends CrudRepository<BookPurchaseEntity, Integer> {
    
}