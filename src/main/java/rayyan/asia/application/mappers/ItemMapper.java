package rayyan.asia.application.mappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rayyan.asia.domain.subclass.Item;
import rayyan.asia.representation.dtos.subclass.ItemDto;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "name", source = "name")
    @Mapping(target = "cost", source = "cost")
    ItemDto toDto(Item item);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "cost", source = "cost")
    Item toEntity(ItemDto dto);
}
