package rayyan.asia.domain.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.bson.types.ObjectId;

import java.time.Instant;

@Data
public abstract class BaseEntity {
    @Id
    private ObjectId id;

    private Instant createdAt;

    private Instant modifiedOn;
}