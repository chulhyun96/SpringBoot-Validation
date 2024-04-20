package hello.itemservice.web.validation.v2;

import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ItemValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return Item.class.isAssignableFrom(aClass);

    }
    @Override
    public void validate(Object o, Errors errors) {

        Item item = (Item) o;
        //validation logic, Field Error
        if (!StringUtils.hasText(item.getItemName())) {
            errors.rejectValue("itemName", "required");
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 10000000) {
            errors.rejectValue("price", "range", new Object[]{1000, 100000}, null);
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            errors.rejectValue("quantity", "max", new Object[]{9999}, null);
        }
        //Global Error
        if (item.getPrice() != null && item.getItemName() != null) {
            int totalPrice = item.getPrice() * item.getQuantity();
            if (totalPrice < 10000) {
                errors.reject("totalPriceMin", new Object[]{10000, totalPrice}, null);
            }
        }
    }
}
