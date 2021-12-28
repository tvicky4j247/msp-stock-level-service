package co.siegerand.stocklevelservice.persistence.repository;

import java.util.Optional;

import co.siegerand.stocklevelservice.persistence.entity.StockLevelEntity;
import org.springframework.data.repository.CrudRepository;

public interface StockLevelRepository extends CrudRepository<StockLevelEntity, Integer> {
    
    Optional<StockLevelEntity> findByBookId(int bookId);

}
