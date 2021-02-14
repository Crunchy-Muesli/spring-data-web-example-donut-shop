package donut.shop.rest.service;

import donut.shop.entity.dto.DonutOrder;
import donut.shop.entity.mongo.CustomerReview;
import donut.shop.entity.mongo.Order;
import donut.shop.entity.relational.Donut;
import donut.shop.entity.repository.mongo.OrderMongoRepository;
import donut.shop.entity.repository.mongo.ReviewMongoRepository;
import donut.shop.entity.repository.relational.DonutRepository;
import javassist.NotFoundException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private OrderMongoRepository orderRepository;
    private DonutRepository donutRepository;
    private ReviewMongoRepository reviewsRepository;

    @Autowired
    public CustomerService(OrderMongoRepository orderRepository,
                           ReviewMongoRepository reviewsMongoRepository, DonutRepository donutRepository) {
        this.orderRepository = orderRepository;
        this.donutRepository = donutRepository;
        this.reviewsRepository = reviewsMongoRepository;
    }

    public Order placeOrder(List<DonutOrder> donuts) {
        Order order = new Order();

        Map<Integer, Donut> realDonuts = checkAndGetMultipleDonuts(donuts);
        order.setDonuts(donuts);
        order.setDate(LocalDateTime.now());

        order.setTotalPrice(realDonuts.entrySet().stream()
                .map(donutEntry -> donutEntry.getKey() * Double.parseDouble(donutEntry.getValue().getPrice()))
                .reduce((double) 0, Double::sum)
                .toString());

        order.set_id(RandomStringUtils.randomAlphabetic(32));

        return orderRepository.save(order);
    }

    public CustomerReview writeReview(String orderId, String review) throws NotFoundException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));

        CustomerReview customerReview = new CustomerReview();
        customerReview.setReview(review);
        customerReview.setOrderId(order.get_id());
        customerReview.set_id(RandomStringUtils.randomAlphabetic(32));

        return reviewsRepository.save(customerReview);
    }

    public List<Donut> getDonuts() {
        return donutRepository.findAll();
    }

    private Map<Integer, Donut> checkAndGetMultipleDonuts(List<DonutOrder> donutOrder) {

        return donutOrder.stream()
                .collect(Collectors.toMap(DonutOrder::getDonutQuantity,
                        order -> donutRepository.findByName(order.getDonutName())
                                .orElseThrow(() -> new RuntimeException("Donut with name " + order.getDonutName() + " not found"))));

    }

}
