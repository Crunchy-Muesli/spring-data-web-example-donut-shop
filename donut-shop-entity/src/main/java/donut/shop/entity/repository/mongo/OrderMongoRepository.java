package donut.shop.entity.repository.mongo;

import donut.shop.entity.mongo.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMongoRepository extends MongoRepository<Order,String> {

}
