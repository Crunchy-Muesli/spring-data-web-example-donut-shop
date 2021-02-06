package donut.shop.entity.relational;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties("orderId")
@Table(name = "orders")
@Entity
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    private String cashier;

    private LocalDateTime date;

    @OneToMany(targetEntity = Donut.class, fetch = FetchType.EAGER)
    private List<Donut> donuts;

}
