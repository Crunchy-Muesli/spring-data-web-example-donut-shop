package donut.shop.rest;

import donut.shop.entity.dto.DonutOrder;
import donut.shop.entity.mongo.CustomerReview;
import donut.shop.entity.mongo.Order;
import donut.shop.entity.relational.Donut;
import donut.shop.rest.service.CustomerService;
import javassist.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {
        CustomerRest.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomerRestTest {

    @MockBean
    private CustomerService customerService;

    @Autowired
    private CustomerRest customerRest;

    @Test
    public void placeOrderTest() {
        DonutOrder donutOrder = new DonutOrder();
        donutOrder.setDonutName("name");
        donutOrder.setDonutQuantity(2);

        Order order = new Order();
        order.set_id("id");
        order.setDonuts(Collections.singletonList(donutOrder));

        doReturn(order).when(customerService).placeOrder(any(List.class));

        ResponseEntity<Order> result = customerRest.placeOrder(Collections.singletonList(donutOrder));

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(order, result.getBody());

        doThrow(new RuntimeException()).when(customerService).placeOrder(any(List.class));

        ResponseEntity<Order> errorResult = customerRest.placeOrder(Collections.singletonList(donutOrder));

        assertNotNull(errorResult);
        assertEquals(HttpStatus.BAD_REQUEST, errorResult.getStatusCode());
        assertNull(errorResult.getBody());
    }

    @Test
    public void writeReviewTest() throws NotFoundException {
        CustomerReview customerReview = new CustomerReview();

        doReturn(customerReview).when(customerService).writeReview(any(String.class), any(String.class));

        ResponseEntity<CustomerReview> result = customerRest.writeReview("id", "review");

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(customerReview, result.getBody());

        doThrow(new RuntimeException()).when(customerService).writeReview(any(String.class),
                any(String.class));

        ResponseEntity<CustomerReview> errorResult = customerRest.writeReview("id", "review");

        assertNotNull(errorResult);
        assertEquals(HttpStatus.NOT_FOUND, errorResult.getStatusCode());
        assertNull(errorResult.getBody());
    }

    @Test
    public void getDonutsTest() {
        Donut donut1 = new Donut();
        donut1.setDonutId(1);
        donut1.setName("donut");
        Donut donut2 = new Donut();
        donut2.setDonutId(2);
        donut2.setName("donut2");

        doReturn(Arrays.asList(donut1, donut2)).when(customerService).getDonuts();

        ResponseEntity<List<Donut>> result = customerRest.getDonuts();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().size());
        assertEquals(donut1, result.getBody().get(0));

        doThrow(new RuntimeException()).when(customerService).getDonuts();

        ResponseEntity<List<Donut>> errorResult = customerRest.getDonuts();

        assertNotNull(errorResult);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResult.getStatusCode());
        assertNull(errorResult.getBody());
    }
}
