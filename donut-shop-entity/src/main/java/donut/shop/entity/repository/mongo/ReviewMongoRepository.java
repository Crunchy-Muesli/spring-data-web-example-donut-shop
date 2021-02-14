package donut.shop.entity.repository.mongo;

import donut.shop.entity.mongo.CustomerReview;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewMongoRepository extends MongoRepository<CustomerReview, String> {

}
