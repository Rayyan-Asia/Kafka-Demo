package rayyan.asia.application.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rayyan.asia.domain.entities.Order;
import rayyan.asia.domain.subclass.EntryItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    private static final Logger LOG = LoggerFactory.getLogger(PdfGeneratorServiceImpl.class);

    @Override
    public byte[] generateInvoice(Order order) throws IOException {
        if (order == null) {
            throw new IllegalArgumentException("order must not be null");
        }

        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                cs.newLineAtOffset(50, 750);
                cs.showText("Invoice");
                cs.endText();

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(50, 720);
                ObjectId id = order.getId();
                cs.showText("Order ID: " + (id != null ? id.toHexString() : "(unknown)") );
                cs.endText();

                // List items
                List<EntryItem> items = order.getItems();
                if (items != null && !items.isEmpty()) {
                    float y = 680f;
                    for (EntryItem ei : items) {
                        if (y < 80) {
                            // new page
                            cs.close();
                            PDPage next = new PDPage();
                            doc.addPage(next);
                            // open a new content stream for the new page
                            try (PDPageContentStream cs2 = new PDPageContentStream(doc, next)) {
                                cs2.beginText();
                                cs2.setFont(PDType1Font.HELVETICA, 12);
                                cs2.newLineAtOffset(50, 720);
                                cs2.showText(formatEntryItem(ei));
                                cs2.endText();
                            }
                            y = 680f;
                        } else {
                            cs.beginText();
                            cs.setFont(PDType1Font.HELVETICA, 12);
                            cs.newLineAtOffset(50, y);
                            cs.showText(formatEntryItem(ei));
                            cs.endText();
                            y -= 20f;
                        }
                    }
                }

            }

            doc.save(baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            LOG.error("Failed to generate PDF for order {}", order.getId(), ex);
            throw ex;
        }
    }

    private String formatEntryItem(EntryItem ei) {
        if (ei == null) return "";
        String name = "(no item)";
        if (ei.getItem() != null) {
            name = ei.getItem().getName();
        }
        return String.format("- %s x%d @ %s", name, ei.getCount(), (ei.getItem() != null ? ei.getItem().getCost() : "?"));
    }
}

