package rayyan.asia.application.mappers;

import org.mapstruct.Mapper;
import rayyan.asia.domain.subclass.EntryItem;
import rayyan.asia.representation.dtos.subclass.EntryItemDto;

@Mapper(componentModel = "spring", uses = {ItemMapper.class})
public interface EntryItemMapper {
    EntryItemDto toDto(EntryItem entryItem);
    EntryItem toEntity(EntryItemDto dto);
}

