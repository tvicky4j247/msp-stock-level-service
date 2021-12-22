package co.siegerand.stocklevelservice.persistence.repository;

import co.siegerand.stocklevelservice.persistence.entity.StockReplenishmentEntity;
import org.springframework.data.repository.CrudRepository;

import co.siegerand.stocklevelservice.model.StockReplenishment;

public interface StockReplenishmentRepository extends CrudRepository<StockReplenishmentEntity, Integer> {
    
}
