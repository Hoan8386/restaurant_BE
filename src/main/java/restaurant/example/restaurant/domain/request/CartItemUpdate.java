package restaurant.example.restaurant.domain.request;

public class CartItemUpdate {
    private Long id;
    private int quantity;

    // getters và setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
