package rayyan.asia.application.services;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rayyan.asia.application.mappers.InvoiceMapper;
import rayyan.asia.domain.entities.Invoice;
import rayyan.asia.domain.entities.Order;
import rayyan.asia.infrastructure.repositories.InvoiceRepository;
import rayyan.asia.infrastructure.repositories.OrderRepository;
import rayyan.asia.representation.dtos.InvoiceDto;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceServiceImpl.class);

    private final InvoiceMapper invoiceMapper;
    private final InvoiceRepository invoiceRepository;
    private final S3Service s3Service;
    private final PdfGeneratorService pdfGeneratorService;
    private final OrderRepository orderRepository;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket:}")
    private String bucket;

    @Value("${aws.endpoint:http://localhost:4566}")
    private String awsEndpoint;

    @Override
    public InvoiceDto getInvoice(ObjectId id) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Invoice not found: " + id));
        return invoiceMapper.toDto(invoice);
    }

    @Override
    @Transactional
    public InvoiceDto updateInvoice(InvoiceDto invoice) {
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
                return;
            }

            // generate PDF bytes using the PdfGeneratorService
            byte[] pdf = pdfGeneratorService.generateInvoice(order);

            // upload to S3
            if (bucket == null || bucket.isBlank()) {
                LOG.error("aws.s3.bucket is not configured; cannot upload invoice for order={}", orderIdHex);
                return;
            }
            String filename = "invoice_" + orderIdHex + ".pdf";
            String s3Key = s3Service.uploadBytes(pdf, filename, "application/pdf");

            // save Invoice
            Invoice invoice = new Invoice();
            invoice.setOrderId(orderId);
            invoice.setS3Key(s3Key);
            invoiceRepository.save(invoice);

            LOG.info("Created invoice for order {} and uploaded to s3 key={}", orderIdHex, s3Key);
        } catch (IOException ex) {
            LOG.error("I/O error while processing order-completed message for {}", orderIdHex, ex);
        } catch (Exception ex) {
            LOG.error("Failed to process order-completed message for {}", orderIdHex, ex);
        }
    }

    @Override
    public String getInvoiceUrl(ObjectId id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found: " + id));
        String s3Key = invoice.getS3Key();
        if (s3Key == null || s3Key.isBlank()) {
            throw new NoSuchElementException("Invoice has no s3 key: " + id);
        }

        if (bucket == null || bucket.isBlank()) {
            throw new IllegalStateException("aws.s3.bucket is not configured");
        }

        // Build a GetObjectRequest and presign it for a short duration
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(getObjectRequest)
                .build();

        var presignedResp = s3Presigner.presignGetObject(presignRequest);
        var presignedUrl = presignedResp.url();
        URI presignedUri;
        try {
            presignedUri = presignedUrl.toURI();
        } catch (URISyntaxException e) {
            presignedUri = URI.create(presignedUrl.toString());
        }
        String query = presignedUri.getRawQuery();

        // Build a guaranteed path-style URL using configured aws.endpoint
        try {
            URI endpointUri = new URI(awsEndpoint);
            String scheme = endpointUri.getScheme() != null ? endpointUri.getScheme() : "http";
            String host = endpointUri.getHost();
            int port = endpointUri.getPort();
            String hostPort = host + (port == -1 ? "" : ":" + port);
            String base = scheme + "://" + hostPort;
            String path = "/" + bucket + "/" + s3Key;
            String finalUrl = base + path + (query != null && !query.isBlank() ? "?" + query : "");
            return finalUrl;
        } catch (URISyntaxException e) {
            LOG.warn("aws.endpoint is not a valid URI ({}), falling back to presigned URL: {}", awsEndpoint, presignedUri.toString());
            return presignedUri.toString();
        }
    }

}
