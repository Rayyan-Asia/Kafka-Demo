package rayyan.asia.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rayyan.asia.domain.entities.Order;
import rayyan.asia.representation.dtos.OrderDto;

@Mapper(componentModel = "spring", uses = {EntryItemMapper.class})
public interface OrderMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "status", source = "status")
    OrderDto toDto(Order order);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "status", source = "status")
    Order toEntity(OrderDto dto);
}

