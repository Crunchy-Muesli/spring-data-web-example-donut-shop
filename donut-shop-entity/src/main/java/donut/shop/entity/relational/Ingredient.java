package donut.shop.entity.relational;

import lombok.Data;

import javax.persistence.*;

@Table(name = "ingredients",uniqueConstraints={@UniqueConstraint(columnNames = {"name"})})
@Entity
@Data
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @ManyToOne(targetEntity=Donut.class,fetch= FetchType.EAGER)
    private Donut donut;

}
