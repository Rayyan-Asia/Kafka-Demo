package rayyan.asia.domain.entities;

import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Document(collection = "invoices")
public class Invoice extends BaseEntity {
    private String s3Location;
}