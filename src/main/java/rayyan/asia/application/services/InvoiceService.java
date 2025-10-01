package rayyan.asia.application.services;

import org.bson.types.ObjectId;
import rayyan.asia.representation.dtos.InvoiceDto;

public interface InvoiceService {

    InvoiceDto getInvoice(ObjectId id);

}
