package donut.shop.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class DonutDto {

    private String name;

    private List<IngredientDto> ingredients;

    private Long price;

    private String description;

}
