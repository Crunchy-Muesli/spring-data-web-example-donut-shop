package donut.shop.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {

    private String customer;

    private LocalDateTime date;

    private List<DonutDto> donuts;

}
