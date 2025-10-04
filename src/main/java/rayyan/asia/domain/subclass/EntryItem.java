package rayyan.asia.domain.subclass;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class EntryItem {

    @NotNull
    public Item item;

    @NotNull
    public int count;
}
