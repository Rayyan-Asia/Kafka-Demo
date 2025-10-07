package rayyan.asia.application.services.order;

import org.bson.types.ObjectId;
import rayyan.asia.representation.dtos.OrderDto;

public interface OrderService {

    OrderDto getOrder(ObjectId id);

    OrderDto upsertDto(OrderDto orderDto);

    OrderDto completeOrder(ObjectId id);

    void deleteOrder(ObjectId id);
}

