package donut.shop.entity.relational;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@JsonIgnoreProperties({"ingredientId", "donuts"})
@Table(name = "ingredients", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
@Entity
@Data
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ingredientId;

    private String name;
}
