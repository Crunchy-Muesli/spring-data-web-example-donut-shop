package donut.shop.entity.relational;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Table(name = "donuts",uniqueConstraints={@UniqueConstraint(columnNames = {"name","description"})})
@Entity
@Data
public class Donut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(targetEntity= Ingredient.class,fetch= FetchType.EAGER)
    private List<Ingredient> ingredients;

    private Long price;

    private String description;

}
