package rayyan.asia.application.services.invoice;

import org.bson.types.ObjectId;
import rayyan.asia.representation.dtos.InvoiceDto;

import java.io.IOException;

public interface InvoiceService {

    InvoiceDto getInvoice(ObjectId id);

    /**
     * Return a presigned URL (or direct URL) that allows downloading the invoice PDF stored in S3.
     * @param id invoice id
     * @return presigned URL string
     */
    String getInvoiceUrl(ObjectId id) throws IOException;

    void handleOrderCompleted(String orderIdHex);
}
