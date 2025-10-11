package rayyan.asia.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;
import lombok.RequiredArgsConstructor;
import rayyan.asia.application.services.order.OrderService;
import rayyan.asia.domain.entities.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class DltLogger {

    private static final Logger LOG = LoggerFactory.getLogger(DltLogger.class);
    private static final Pattern OBJECT_ID_PATTERN = Pattern.compile("\\b[0-9a-fA-F]{24}\\b");

    private final OrderService orderService;

    // allow topic name override from properties; defaults to 'order-completed'
    @Value("${app.topic.order-completed:order-completed}")
    private String orderCompletedTopicName;

    @KafkaListener(topicPattern = ".*\\.DLQ", groupId = "${spring.kafka.consumer.group-id:demo-group}")
    public void handleDlq(ConsumerRecord<String, Object> record) {
        LOG.warn("DLQ message received - topic={} partition={} offset={}",
                record.topic(), record.partition(), record.offset());

        LOG.warn("DLQ payload: {}", record.value());

        record.headers().forEach(header -> {
            String value = header.value() == null ? "null" : new String(header.value());
            LOG.warn("DLQ header {}={}", header.key(), value);
        });


        if (isFromOrderCompleted(record.topic())) {
            updateEntityStatus(record);
        }
    }

    private void updateEntityStatus(ConsumerRecord<String, Object> record) {
        try {
            String orderIdHex = extractObjectId(record.value());
            if (orderIdHex == null) {
                LOG.warn("Could not find an ObjectId in DLQ payload for topic {}", record.topic());
                return;
            }

            if (!ObjectId.isValid(orderIdHex)) {
                LOG.warn("Found candidate id but it's not a valid ObjectId: {}", orderIdHex);
                return;
            }

            ObjectId id = new ObjectId(orderIdHex);
            var order = orderService.getOrder(id);
            if (order == null) {
                LOG.warn("No order found with id={} while handling DLQ for topic={}", orderIdHex, record.topic());
                return;
            }

            // set status to the enum value present in the project
            order.setStatus(Status.FAILED);
            orderService.upsertDto(order);
            LOG.info("Marked order {} as Failed due to DLQ entry from topic={}", orderIdHex, record.topic());
        } catch (Exception ex) {
            LOG.error("Error while processing DLQ entry for topic={}", record.topic(), ex);
        }
    }

    private boolean isFromOrderCompleted(String dlqTopic) {
        if (dlqTopic == null) return false;
        // DLQ topics are created as <topic>.DLQ - accept either the exact DLQ or any topic that contains the base name
        String base = orderCompletedTopicName.trim();
        if (base.isEmpty()) base = "order-completed";
        return dlqTopic.equals(base + ".DLQ") || dlqTopic.contains(base);
    }

    private String extractObjectId(Object value) {
        if (value == null) return null;
        String s = value.toString();
        Matcher m = OBJECT_ID_PATTERN.matcher(s);
        if (m.find()) return m.group();
        return null;
    }
}
