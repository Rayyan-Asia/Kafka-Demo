package rayyan.asia.application.services.order;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rayyan.asia.application.mappers.OrderMapper;
import rayyan.asia.domain.entities.Status;
import rayyan.asia.infrastructure.repositories.OrderRepository;
import rayyan.asia.representation.dtos.OrderDto;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements  OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.topic.order-completed:order-completed}")
    private String orderCompletedTopic;

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
    public OrderDto completeOrder(ObjectId id) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + id));

        order.setStatus(Status.COMPLETED);
        orderRepository.save(order);
        LOG.info("Order {} marked as completed.", id);
        publishCreateInvoice(id);
        return orderMapper.toDto(order);
    }

    private void publishCreateInvoice(ObjectId id) {
        try {
            String key = id.toHexString();
            kafkaTemplate.send(orderCompletedTopic, key).addCallback(
                    result -> LOG.info("Published order-completed message for {} to topic {}", key, orderCompletedTopic),
                    ex -> LOG.error("Failed to publish order-completed message for {}", key, ex)
            );
        } catch (Exception e) {
            LOG.error("Exception while publishing kafka message for order {}", id, e);
        }
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
