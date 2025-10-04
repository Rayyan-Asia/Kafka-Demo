package rayyan.asia.representation.dtos.subclass;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ItemDto{

    @NotEmpty
    @Size(min = 1, max = 100)
    private String name;

    @NotNull
    private float cost;
}
