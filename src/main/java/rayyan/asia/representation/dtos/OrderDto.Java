package rayyan.asia.representation.dtos;

import lombok.Data;
import org.bson.types.ObjectId;
import rayyan.asia.domain.entities.Status;
import rayyan.asia.representation.dtos.subclass.EntryItemDto;

import javax.validation.Valid;
import java.util.List;

@Data
public class OrderDto {

    @Valid
    private ObjectId id;
    private List<EntryItemDto> items;
    private Status status = Status.PENDING;
}