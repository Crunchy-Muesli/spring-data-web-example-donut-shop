package donut.shop.entity.mongo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Document(collection = "reviews")
@Data
public class CustomerReview {

    //overriding the obligatory mongodb "_id" field
    @Id
    private String _id;

    private String orderId;

    private String review;
}
