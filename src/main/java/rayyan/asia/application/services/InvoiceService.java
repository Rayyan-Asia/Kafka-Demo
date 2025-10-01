package rayyan.asia.application.services;

import org.bson.types.ObjectId;
import rayyan.asia.representation.dtos.InvoiceDto;

public interface InvoiceService {

    public InvoiceDto getInvoice(ObjectId id);

}
