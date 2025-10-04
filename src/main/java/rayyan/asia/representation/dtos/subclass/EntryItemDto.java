package rayyan.asia.representation.dtos.subclass;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class EntryItemDto {
    @NotNull
    public ItemDto item;
    @NotNull
    public int count;
}
