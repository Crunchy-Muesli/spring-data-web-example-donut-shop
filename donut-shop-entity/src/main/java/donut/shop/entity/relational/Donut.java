package donut.shop.entity.relational;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@JsonIgnoreProperties("donutId")
@Table(name = "donuts", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "description"})})
@Entity
@Data
public class Donut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int donutId;

    private String name;

    private String price;

    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "donuts_ingredients",
            joinColumns = {@JoinColumn(name = "donut_id")},
            inverseJoinColumns = {@JoinColumn(name = "ingredient_id")})
    private Set<Ingredient> ingredients;
}
