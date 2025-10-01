package rayyan.asia.application.services;

import rayyan.asia.infrastructure.repositories.InvoiceRepository;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public InvoiceDto getInvoice(ObjectId id) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Invoice not found: " + id));
        return mapToDto(invoice);
    }

    private InvoiceDto mapToDto(Invoice invoice) {
        InvoiceDto dto = new InvoiceDto();
        dto.setId(invoice.getId);
        return dto;
    }
}