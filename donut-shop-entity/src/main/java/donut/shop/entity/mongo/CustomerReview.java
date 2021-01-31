package donut.shop.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "reviews")
@Data
@AllArgsConstructor
public class CustomerReview {

    private int orderId;

    private String review;
}
