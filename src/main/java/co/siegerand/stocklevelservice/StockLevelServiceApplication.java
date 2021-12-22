package co.siegerand.stocklevelservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class StockLevelServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockLevelServiceApplication.class, args);
    }

    @Bean
    public Scheduler getScheduler() {
        return Schedulers.newBoundedElastic(10, 100, "thread-pool");
    }

}
