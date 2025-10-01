package rayyan.asia.domain.entities;

import org.bson.types.ObjectId;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Invoice {
    private ObjectId id;
}
