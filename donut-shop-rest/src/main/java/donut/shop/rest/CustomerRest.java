package donut.shop.rest;

import donut.shop.entity.dto.DonutOrder;
import donut.shop.entity.mongo.CustomerReview;
import donut.shop.entity.mongo.Order;
import donut.shop.entity.relational.Donut;
import donut.shop.rest.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static donut.shop.constant.DonutShopConstant.EXCEPTION;


@RestController
@RequestMapping("/customer/")
public class CustomerRest {

    Logger logger = LoggerFactory.getLogger(CustomerRest.class);

    private CustomerService customerService;

    @Autowired
    public CustomerRest(CustomerService service) {
        this.customerService = service;
    }

    @PostMapping("place-order")
    public ResponseEntity<Order> placeOrder(@RequestBody List<DonutOrder> req) {
        try {
            Order res = customerService.placeOrder(req);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header(EXCEPTION, e.getMessage())
                    .body(null);
        }
    }

    @PostMapping("write-review/{orderId}")
    public ResponseEntity<CustomerReview> writeReview(@PathVariable("orderId") String orderId,
                                                      String review) {

        try {
            CustomerReview res = customerService.writeReview(orderId, review);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header(EXCEPTION, e.getMessage())
                    .body(null);
        }
    }

    @GetMapping("get-donuts")
    public ResponseEntity<List<Donut>> getDonuts() {
        try {
            List<Donut> res = customerService.getDonuts();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header(EXCEPTION, e.getMessage())
                    .body(null);
        }
    }

}
