package co.siegerand.stocklevelservice.persistence.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import co.siegerand.stocklevelservice.persistence.entity.StockLevelEntity;
import org.springframework.data.repository.CrudRepository;

public interface StockLevelRepository extends CrudRepository<StockLevelEntity, Integer> {
    
    Optional<StockLevelEntity> findByBookId(int bookId);

    @Transactional
    void deleteAllByBookId(int bookId);

}
