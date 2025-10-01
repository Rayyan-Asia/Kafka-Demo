package rayyan.asia.application.mappers;

import org.mapstruct.Mapper;
import rayyan.asia.domain.entities.Order;
import rayyan.asia.representation.dtos.OrderDto;

@Mapper(componentModel = "spring", uses = {EntryItemMapper.class})
public interface OrderMapper {
    OrderDto toDto(Order order);
    Order toEntity(OrderDto dto);
}

