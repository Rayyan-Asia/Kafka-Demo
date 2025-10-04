package rayyan.asia.application.services;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rayyan.asia.application.mappers.OrderMapper;
import rayyan.asia.infrastructure.repositories.OrderRepository;
import rayyan.asia.representation.dtos.OrderDto;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements  OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final MongoTemplate mongoTemplate;

    @Override
    public OrderDto getOrder(ObjectId id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + id));
    }

    @Override
    @Transactional
    public OrderDto upsertDto(OrderDto orderDto) {

        if (orderDto.getId() != null && !orderRepository.existsById(orderDto.getId())) {
            throw new NoSuchElementException("Order not found: " + orderDto.getId());
        }

        var entity = orderMapper.toEntity(orderDto);
        orderRepository.save(entity);
        return orderMapper.toDto(entity);
    }

    @Override
    @Transactional
    public void deleteOrder(ObjectId id) {
        if (!orderRepository.existsById(id)) {
            throw new NoSuchElementException("Order not found: " + id);
        }
        orderRepository.deleteById(id);
    }
}
