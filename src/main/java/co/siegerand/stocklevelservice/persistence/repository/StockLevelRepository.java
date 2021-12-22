package co.siegerand.stocklevelservice.persistence.repository;

import java.util.Optional;

import co.siegerand.stocklevelservice.persistence.entity.StockLevelEntity;
import org.springframework.data.repository.CrudRepository;

import co.siegerand.stocklevelservice.model.StockLevel;

public interface StockLevelRepository extends CrudRepository<StockLevelEntity, Integer> {
    
    Optional<StockLevel> findByBookId(int bookId);

}
