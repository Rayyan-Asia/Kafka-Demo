package rayyan.asia.infrastructure.migrations;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id="init-invoice-collection", order="002", author="rayyan")
public class InitInvoiceCollection {

    private final MongoTemplate mongoTemplate;

    public InitInvoiceCollection(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Execution
    public void changeSet() {
        mongoTemplate.createCollection("invoices");
    }

    @RollbackExecution
    public void rollback() {
        mongoTemplate.dropCollection("invoices");
    }
}