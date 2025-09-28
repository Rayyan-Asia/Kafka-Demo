package rayyan.asia.infrastructure.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import rayyan.asia.domain.entities.Order;

public interface OrderRepository extends MongoRepository<Order, ObjectId> {
}
