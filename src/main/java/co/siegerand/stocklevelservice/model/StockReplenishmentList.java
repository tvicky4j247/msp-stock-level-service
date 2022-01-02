package co.siegerand.stocklevelservice.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StockReplenishmentList {
    
    private List<StockReplenishment> stockReplenishmentList;
    private String serviceAddress;

}
