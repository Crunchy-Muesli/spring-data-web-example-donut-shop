package donut.shop.rest.service;

import donut.shop.entity.dto.DonutDto;
import donut.shop.entity.dto.OrderDto;
import donut.shop.entity.mongo.CustomerReview;
import donut.shop.entity.relational.Donut;
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

@Service
public class CustomerService {

    private OrderRepository orderRepository;
    private DonutRepository donutRepository;
    private ReviewsMongoRepository reviewsRepository;
    private ModelMapper modelMapper;

    @Autowired
    public CustomerService(OrderRepository orderRepository, ReviewsMongoRepository reviewsMongoRepository, DonutRepository donutRepository, ModelMapper modelMapper){
        this.orderRepository = orderRepository;
        this.donutRepository = donutRepository;
        this.reviewsRepository = reviewsMongoRepository;
        this.modelMapper = modelMapper;
    }

    public Order placeOrder(OrderDto req) {
        Order order = modelMapper.map(req,Order.class);
        return orderRepository.save(order);
    }

    public CustomerReview writeReview(String orderId, String review) throws NotFoundException {
        Order order = orderRepository.findById(orderId)
               .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));

        return new CustomerReview(order.getId(), review);
    }

    public List<DonutDto> getDonuts(){
        List<Donut> donuts = donutRepository.findAll();
        return Arrays.asList(modelMapper.map(donuts,DonutDto[].class));
    }

}
