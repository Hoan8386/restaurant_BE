package restaurant.example.restaurant.domain.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultPaginationDataDTO {
    private Meta meta;
    private Object result;
}
