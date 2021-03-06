package donut.shop.rest.service;


import donut.shop.entity.dto.DonutOrder;
import donut.shop.entity.mongo.CustomerReview;
import donut.shop.entity.mongo.Order;
import donut.shop.entity.relational.Donut;
import donut.shop.entity.repository.mongo.OrderMongoRepository;
import donut.shop.entity.repository.mongo.ReviewMongoRepository;
import donut.shop.entity.repository.relational.DonutRepository;
import javassist.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {
        CustomerService.class,
        DonutRepository.class,
        ReviewMongoRepository.class,
        OrderMongoRepository.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomerServiceTest {

    @MockBean
    private DonutRepository donutRepository;

    @MockBean
    private ReviewMongoRepository reviewMongoRepository;

    @MockBean
    private OrderMongoRepository orderMongoRepository;

    @Autowired
    private CustomerService customerService;

    @Test
    public void placeOrderTest() {

        DonutOrder donutOrder1 = new DonutOrder();
        donutOrder1.setDonutQuantity(2);
        donutOrder1.setDonutName("name1");

        Donut donut1 = new Donut();
        donut1.setPrice("2");

        DonutOrder donutOrder2 = new DonutOrder();
        donutOrder2.setDonutQuantity(1);
        donutOrder2.setDonutName("name2");

        Donut donut2 = new Donut();
        donut2.setPrice("3");

        doReturn(Optional.of(donut1)).when(donutRepository).findByName("name1");
        doReturn(Optional.of(donut2)).when(donutRepository).findByName("name2");

        doAnswer(
                (Answer<Order>)
                        invocation -> {
                            Object[] args = invocation.getArguments();
                            Order order = (Order) args[0];
                            assertEquals("7.0", order.getTotalPrice());
                            return order;
                        })
                .when(orderMongoRepository)
                .save(any(Order.class));

        customerService.placeOrder(Arrays.asList(donutOrder1, donutOrder2));

        //Donut not found
        doThrow(new RuntimeException()).when(donutRepository).findByName("name1");
        assertThrows(RuntimeException.class, () -> customerService.placeOrder(Arrays.asList(donutOrder1,
                donutOrder2)));
    }

    @Test
    public void writeReviewTest() throws NotFoundException {
        CustomerReview customerReview = new CustomerReview();

        doReturn(Optional.of(new Order())).when(orderMongoRepository).findById(any(String.class));
        doReturn(customerReview).when(reviewMongoRepository).save(any(CustomerReview.class));

        CustomerReview result = customerService.writeReview("id", "review");

        assertNotNull(result);
        assertEquals(customerReview, result);

        //Order not found

        doThrow(new RuntimeException()).when(orderMongoRepository).findById(any(String.class));
        assertThrows(RuntimeException.class, () -> customerService.writeReview("id", "review"));
    }

    @Test
    public void getDonutsTest() {
        List<Donut> donuts = Collections.singletonList(new Donut());
        doReturn(donuts).when(donutRepository).findAll();

        List<Donut> result = customerService.getDonuts();

        assertNotNull(result);
        assertEquals(donuts, result);
    }
}
