package co.siegerand.stocklevelservice.persistence.repository;

import co.siegerand.stocklevelservice.persistence.entity.StockReplenishmentEntity;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface StockReplenishmentRepository extends CrudRepository<StockReplenishmentEntity, Integer> {

    void deleteAllByBookId(int bookId);
    
    List<StockReplenishmentEntity> findAllByBookId(int bookId);
    
}
