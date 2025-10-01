package rayyan.asia.representation.dtos.subclass;

import lombok.Data;
import rayyan.asia.representation.dtos.subclass.ItemDto;

@Data
public class EntryItemDto {
    public ItemDto item;
    public int count;
}
