package rayyan.asia.application.services;

import org.bson.types.ObjectId;
import rayyan.asia.representation.dtos.OrderDto;

public interface OrderService {

    OrderDto getOrder(ObjectId id);
    
}
