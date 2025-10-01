package rayyan.asia.domain.entities;

import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.experimental.Accessors;
import rayyan.asia.domain.subclass.EntryItem;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Document(collection = "orders")
public class Order extends BaseEntity {
    private List<EntryItem> items;
    private Status status = Status.PENDING;
}