package rayyan.asia.application.services.invoice;

import org.bson.types.ObjectId;
import rayyan.asia.representation.dtos.InvoiceDto;

public interface InvoiceService {

    InvoiceDto getInvoice(ObjectId id);

    InvoiceDto updateInvoice(InvoiceDto invoice);

    void deleteInvoice(ObjectId id);

    void handleOrderCompleted(String orderIdHex);

    /**
     * Return a presigned URL (or direct URL) that allows downloading the invoice PDF stored in S3.
     * @param id invoice id
     * @return presigned URL string
     */
    String getInvoiceUrl(ObjectId id);
}
