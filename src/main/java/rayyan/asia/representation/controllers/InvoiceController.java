package rayyan.asia.representation.controllers;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import rayyan.asia.application.services.InvoiceService;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/{id}/url")
    public ResponseEntity<Map<String, String>> getInvoiceUrl(@PathVariable("id") String id) {
        if (!ObjectId.isValid(id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid invoice id"));
        }
        try {
            String url = invoiceService.getInvoiceUrl(new ObjectId(id));
            return ResponseEntity.ok(Map.of("url", url));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }
}
