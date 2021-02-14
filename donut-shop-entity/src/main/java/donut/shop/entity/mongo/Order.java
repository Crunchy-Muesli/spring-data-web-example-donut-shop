package donut.shop.entity.mongo;

import donut.shop.entity.dto.DonutOrder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
@Data
public class Order {

    //overriding the obligatory mongodb "_id" field
    @Id
    private String _id;

    private String totalPrice;

    private LocalDateTime date;

    private List<DonutOrder> donuts;
}
