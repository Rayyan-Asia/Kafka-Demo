package rayyan.asia.domain.entities;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Id;
import org.bson.types.ObjectId;
import java.time.Instant;

@Data
public abstract class BaseEntity {
    @Id
    private ObjectId id;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant modifiedOn;
}