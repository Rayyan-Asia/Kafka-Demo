package rayyan.asia.application.services;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rayyan.asia.application.mappers.InvoiceMapper;
import rayyan.asia.domain.entities.Invoice;
import rayyan.asia.infrastructure.repositories.InvoiceRepository;
import rayyan.asia.representation.dtos.InvoiceDto;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceMapper invoiceMapper;
    private final InvoiceRepository invoiceRepository;

    @Override
    public InvoiceDto getInvoice(ObjectId id) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Invoice not found: " + id));
        return invoiceMapper.toDto(invoice);
    }

    @Override
    @Transactional
    public InvoiceDto upsertInvoice(InvoiceDto invoice) {
        if (invoice.getId() != null && !invoiceRepository.existsById(invoice.getId())) {
            throw new NoSuchElementException("Invoice not found: " + invoice.getId());
        }

        var entity = invoiceMapper.toEntity(invoice);
        invoiceRepository.save(entity);
        return invoiceMapper.toDto(entity);
    }

    @Override
    @Transactional
    public void deleteInvoice(ObjectId id) {
        if (!invoiceRepository.existsById(id)) {
            throw new NoSuchElementException("Invoice not found: " + id);
        }
        invoiceRepository.deleteById(id);
    }
}

