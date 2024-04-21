package hello.itemservice.domain.item;

import hello.itemservice.web.validation.form.ItemSaveRequest;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Item {

    @NotNull
    private Long id;

    @NotBlank
    private String itemName;

    @NotNull
    @Range(max = 100000, min = 1000)
    private Integer price;

    @NotNull
    @Max(value = 9999)
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
    public static Item from(ItemSaveRequest request) {
        return new Item(request.getItemName(), request.getPrice(), request.getQuantity());
    }
}
