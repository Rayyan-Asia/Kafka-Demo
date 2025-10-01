package rayyan.asia.application.services;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import rayyan.asia.application.mappers.OrderMapper;
import rayyan.asia.infrastructure.repositories.OrderRepository;
import rayyan.asia.representation.dtos.OrderDto;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements  OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderDto getOrder(ObjectId id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + id));
    }
}
