package rayyan.asia.application.services.pdf;

import rayyan.asia.domain.entities.Order;

import java.io.IOException;

public interface PdfGeneratorService {
    /**
     * Generate an invoice PDF for the given order.
     * @param order order entity with items and other details
     * @return PDF bytes
     * @throws IOException on failure
     */
    byte[] generateInvoice(Order order) throws IOException;
}

