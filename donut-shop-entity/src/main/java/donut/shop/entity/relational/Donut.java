package donut.shop.entity.relational;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties({"order","donutId"})
@Table(name = "donuts",uniqueConstraints={@UniqueConstraint(columnNames = {"name","description"})})
@Entity
@Data
public class Donut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int donutId;

    private String name;

    private BigDecimal price;

    private String description;

    @OneToMany(targetEntity = Ingredient.class, fetch = FetchType.EAGER)
    private Set<Ingredient> ingredients;
}
