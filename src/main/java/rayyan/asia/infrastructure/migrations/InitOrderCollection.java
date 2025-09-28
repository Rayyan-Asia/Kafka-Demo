package rayyan.asia.infrastructure.migrations;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id="init-order-collection", order="001", author="rayyan")
public class InitOrderCollection {

    private final MongoTemplate mongoTemplate;

    public InitOrderCollection(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Execution
    public void changeSet() {
        mongoTemplate.createCollection("orders");
    }

    @RollbackExecution
    public void rollback() {
        mongoTemplate.dropCollection("orders");
    }
}
