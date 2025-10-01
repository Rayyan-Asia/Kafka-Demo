package rayyan.asia.application.mappers;

import org.mapstruct.Mapper;
import rayyan.asia.domain.entities.Invoice;
import rayyan.asia.representation.dtos.InvoiceDto;

@Mapper(componentModel = "spring", uses = {EntryItemMapper.class})
public interface InvoiceMapper {
    InvoiceDto toDto(Invoice invoice);
    Invoice toEntity(InvoiceDto dto);
}

