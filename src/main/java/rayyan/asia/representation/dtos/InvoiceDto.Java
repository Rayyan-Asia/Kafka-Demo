package rayyan.asia.representation.dtos;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class InvoiceDto {
    private ObjectId id;
    private String s3Location;
}