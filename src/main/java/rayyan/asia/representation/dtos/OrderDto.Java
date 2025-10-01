package rayyan.asia.representation.dtos;

import lombok.Data;
import org.bson.types.ObjectId;
import rayyan.asia.representation.dtos.subclass.EntryItemDto;
import java.util.List;

@Data
public class OrderDto {
    private ObjectId id;
    private List<EntryItemDto> items;
}