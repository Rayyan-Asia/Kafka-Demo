package rayyan.asia.application.services.invoice;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import rayyan.asia.application.mappers.InvoiceMapper;
import rayyan.asia.application.services.pdf.PdfGeneratorService;
import rayyan.asia.application.services.storage.S3Service;
import rayyan.asia.domain.entities.Invoice;
import rayyan.asia.domain.entities.Order;
import rayyan.asia.infrastructure.repositories.InvoiceRepository;
import rayyan.asia.infrastructure.repositories.OrderRepository;
import rayyan.asia.representation.dtos.InvoiceDto;
import java.io.IOException;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceServiceImpl.class);
    private final InvoiceMapper invoiceMapper;
    private final InvoiceRepository invoiceRepository;
    private final S3Service s3Service;
    private final PdfGeneratorService pdfGeneratorService;
    private final OrderRepository orderRepository;

    @Override
    public InvoiceDto getInvoice(ObjectId id) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Invoice not found: " + id));
        return invoiceMapper.toDto(invoice);
    }

    @Override
    public String getInvoiceUrl(ObjectId id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found: " + id));
        return s3Service.getFileUrl(invoice.getS3Key());
    }

    @KafkaListener(topics = "${app.topic.order-completed}", groupId = "${spring.kafka.consumer.group-id:demo-group}")
    public void handleOrderCompleted(String orderIdHex) {
        LOG.info("Received order-completed message for orderId={}", orderIdHex);
        if (orderIdHex == null || orderIdHex.isBlank()) {
            LOG.warn("Empty orderId in order-completed message, ignoring");
            return;
        }

        if (!ObjectId.isValid(orderIdHex)) {
            LOG.warn("Invalid ObjectId received in order-completed message: {}", orderIdHex);
            return;
        }

        ObjectId orderId = new ObjectId(orderIdHex);

        try {
            // fetch full order
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                LOG.warn("Order not found in DB for id={}, skipping invoice generation", orderIdHex);
                throw new NoSuchElementException("Order not found: " + orderIdHex);
            }

            // generate PDF bytes using the PdfGeneratorService
            byte[] pdf = pdfGeneratorService.generateInvoice(order);

            String filename = "invoice_" + orderIdHex + ".pdf";
            String s3Key = s3Service.uploadBytes(pdf, filename, "application/pdf");

            saveInvoice(orderId, s3Key);

            LOG.info("Created invoice for order {} and uploaded to s3 key={}", orderIdHex, s3Key);
        } catch (IOException ex) {
            LOG.error("I/O error while processing order-completed message for {}", orderIdHex, ex);
            // rethrow so the error handler can send the record to DLQ
            throw new RuntimeException(ex);
        } catch (Exception ex) {
            LOG.error("Failed to process order-completed message for {}", orderIdHex, ex);
            // rethrow so the error handler can send the record to DLQ
            throw new RuntimeException(ex);
        }
    }

    private void saveInvoice(ObjectId orderId, String s3Key) {
        Invoice invoice = new Invoice();
        invoice.setOrderId(orderId);
        invoice.setS3Key(s3Key);
        invoiceRepository.save(invoice);
    }
}
