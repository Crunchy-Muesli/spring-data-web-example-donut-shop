package donut.shop.entity.relational;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@JsonIgnoreProperties("ingredientId")
@Table(name = "ingredients", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
@Entity
@Data
public class Ingredient {

    @Id
    // Starting from index 9 as we have already 8 ingredients from the flyway migration
    @SequenceGenerator(initialValue = 9,
            allocationSize = 1,
            name = "ingredient_sequence",
            sequenceName = "ingredient_sequence")
    @GeneratedValue(generator = "ingredient_sequence")
    private int ingredientId;

    private String name;
}
