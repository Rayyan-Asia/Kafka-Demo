package rayyan.asia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@EnableMongoRepositories(basePackages = "rayyan.asia.infrastructure")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
