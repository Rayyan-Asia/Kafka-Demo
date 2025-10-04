package rayyan.asia.domain.components;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import rayyan.asia.domain.entities.BaseEntity;

import java.time.Instant;

@Component
class BaseEntityListener extends AbstractMongoEventListener<BaseEntity> {

    private final org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    public BaseEntityListener(org.springframework.data.mongodb.core.MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void onBeforeConvert(BeforeConvertEvent<BaseEntity> event) {
        BaseEntity entity = event.getSource();
        Instant now = Instant.now();

        if (entity.getId() == null) {
            if (entity.getCreatedAt() == null) {
                entity.setCreatedAt(now);
            }
            entity.setModifiedOn(now);
            return;
        }

        BaseEntity existing = mongoTemplate.findById(entity.getId(), entity.getClass());

        entity.setCreatedAt(existing == null ? now : existing.getCreatedAt());
        entity.setModifiedOn(now);
    }
}