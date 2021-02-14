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
    // Starting from index 4 as we have already 3 donuts from the flyway migration
    @SequenceGenerator(initialValue = 4,
            allocationSize = 1,
            name = "donut_sequence",
            sequenceName = "donut_sequence")
    @GeneratedValue(generator = "donut_sequence")
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
