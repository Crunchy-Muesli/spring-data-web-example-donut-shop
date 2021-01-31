package donut.shop.entity.relational;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "orders")
@Entity
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String cashier;

    private LocalDateTime date;

    @OneToMany(targetEntity=Donut.class,fetch=FetchType.LAZY)
    private List<Donut> donuts;

}
