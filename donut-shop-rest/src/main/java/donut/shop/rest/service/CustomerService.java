package donut.shop.rest.service;

import donut.shop.entity.mongo.CustomerReview;
import donut.shop.entity.relational.Donut;
import donut.shop.entity.relational.Ingredient;
import donut.shop.entity.relational.Order;
import donut.shop.entity.repository.mongo.ReviewsMongoRepository;
import donut.shop.entity.repository.relational.DonutRepository;
import donut.shop.entity.repository.relational.OrderRepository;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private OrderRepository orderRepository;
    private DonutRepository donutRepository;
    private ReviewsMongoRepository reviewsRepository;

    @Autowired
    public CustomerService(OrderRepository orderRepository, ReviewsMongoRepository reviewsMongoRepository, DonutRepository donutRepository){
        this.orderRepository = orderRepository;
        this.donutRepository = donutRepository;
        this.reviewsRepository = reviewsMongoRepository;
    }

    public Order placeOrder(Order order) {
        order.setDonuts(getRealDonuts(order.getDonuts()));
        return orderRepository.save(order);
    }

    public CustomerReview writeReview(String orderId, String review) throws NotFoundException {
        Order order = orderRepository.findById(orderId)
               .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));

        return new CustomerReview(order.getOrderId(), review);
    }

    public List<Donut> getDonuts(){
        return donutRepository.findAll();
    }

    private List<Donut> getRealDonuts(List<Donut> donuts) {
        return donuts.stream()
                .map(donut -> donutRepository.findByName(donut.getName())
                        .orElseThrow(() -> new RuntimeException("Donut with name " + donut.getName() + " not found")))
                .collect(Collectors.toList());
    }

}
