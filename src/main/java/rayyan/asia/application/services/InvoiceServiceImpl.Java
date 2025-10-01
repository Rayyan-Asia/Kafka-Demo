package rayyan.asia.application.services;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import rayyan.asia.domain.entities.Invoice;
import rayyan.asia.infrastructure.repositories.InvoiceRepository;
import rayyan.asia.representation.dtos.InvoiceDto;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Override
    public InvoiceDto getInvoice(ObjectId id) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Invoice not found: " + id));
        return mapToDto(invoice);
    }

    private InvoiceDto mapToDto(Invoice invoice) {
        InvoiceDto dto = new InvoiceDto();
        dto.setId(invoice.getId());

        return dto;
    }
}

