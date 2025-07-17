package restaurant.example.restaurant.domain.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResOrder {
    private Long id;

    private double totalPrice;

    private String receiverName;

    private String receiverAddress;

    private String receiverPhone;

    private Instant date;

    private String status;

}
