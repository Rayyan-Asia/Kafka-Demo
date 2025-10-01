package rayyan.asia.application.mappers;

import org.mapstruct.Mapper;
import rayyan.asia.domain.subclass.Item;
import rayyan.asia.representation.dtos.subclass.ItemDto;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemDto toDto(Item item);
    Item toEntity(ItemDto dto);
}

