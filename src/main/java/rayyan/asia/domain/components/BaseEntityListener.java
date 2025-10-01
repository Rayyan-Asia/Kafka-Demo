package rayyan.asia.domain.components;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import rayyan.asia.domain.entities.BaseEntity;

import java.time.Instant;

@Component
class BaseEntityListener extends AbstractMongoEventListener<BaseEntity> {
    @Override
    public void onBeforeConvert(BeforeConvertEvent<BaseEntity> event) {
        BaseEntity entity = event.getSource();
        Instant now = Instant.now();
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }
        entity.setModifiedOn(now);
    }
}