package rayyan.asia.representation.controllers;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rayyan.asia.application.services.order.OrderService;
import rayyan.asia.representation.dtos.OrderDto;

import javax.annotation.Nonnull;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public OrderDto getOrder(@PathVariable("id") String id) {
        if (!ObjectId.isValid(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order id: " + id);
        }
        return orderService.getOrder(new ObjectId(id));
    }

    @PostMapping
    public OrderDto createOrder(@Nonnull @Valid @RequestBody OrderDto orderDto) {
        return orderService.upsertDto(orderDto);
    }

    @PutMapping
    public OrderDto updateOrder(@Nonnull @Valid @RequestBody OrderDto orderDto) {
        if (orderDto.getId() == null) {
            throw new IllegalArgumentException("Order ID must be provided for update.");
        }
        if (!ObjectId.isValid(orderDto.getId().toString())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order id: " + orderDto.getId());
        }
        return orderService.upsertDto(orderDto);
    }

    // Mark Order as completed
    @PutMapping("/complete/{id}")
    public OrderDto completeOrder(@PathVariable("id") String id) {
        if (!ObjectId.isValid(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order id: " + id);
        }
        return orderService.completeOrder(new ObjectId(id));
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable("id") String id) {
        if (!ObjectId.isValid(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order id: " + id);
        }
        orderService.deleteOrder(new ObjectId(id));
    }

}
