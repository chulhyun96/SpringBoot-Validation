package hello.itemservice.web.validation.v2;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;import org.springframework.validation.FieldError;import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;

    @InitBinder
    public void init(WebDataBinder dataBinder) {
        dataBinder.addValidators(itemValidator);
    }
    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    /*@PostMapping("/add")*/
    public String addItemV1(@ModelAttribute Item item,BindingResult bindingResult, RedirectAttributes redirectAttributes) {


        //validation logic, Field Error
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("request","itemName","상품 이름은 필수 입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 10000000) {
            bindingResult.addError(new FieldError("request","price","ERROR Price."));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("request","quantity","ERROR Quantity."));
        }
        //Global Error
        if (item.getPrice() != null && item.getItemName() != null) {
            int totalPrice = item.getPrice( ) * item.getQuantity();
            if (totalPrice < 10000) {
                bindingResult.addError(new ObjectError("request","ERROR Price * Quantity."));
            }
        }

        // if validation failed, show AddForm again
        if (bindingResult.hasErrors()) {
            return "validation/v2/addForm";
        }
        // success logic
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
    /*@PostMapping("/add")*/
    public String addItemV2(@ModelAttribute Item item,BindingResult bindingResult, RedirectAttributes redirectAttributes) {


        //validation logic, Field Error
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("request","itemName",item.getItemName(),false, null,null,"상품 이름은 필수 입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 10000000) {
            bindingResult.addError(new FieldError("request","price",item.getPrice(),false, null,null,"ERROR Price."));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("request","quantity", item.getQuantity(),false, null,null,"ERROR Quantity."));
        }
        //Global Error
        if (item.getPrice() != null && item.getItemName() != null) {
            int totalPrice = item.getPrice( ) * item.getQuantity();
            if (totalPrice < 10000) {
                bindingResult.addError(new ObjectError("request",null,null,"ERROR Price * Quantity."));
            }
        }

        // if validation failed, show AddForm again
        if (bindingResult.hasErrors()) {
            return "validation/v2/addForm";
        }
        // success logic
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /*@PostMapping("/add")*/
    public String addItemV3(@ModelAttribute Item item,BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.info("BindingResult Errors = {}", bindingResult);
            return "validation/v2/addForm";
        }
        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());

        //validation logic, Field Error
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("request","itemName",item.getItemName(),false, new String[]{"required.request.itemName"},null,null));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 10000000) {
            bindingResult.addError(new FieldError("request","price",item.getPrice(),false, new String[]{"range.request.price"},new Object[]{1000,1000000},null));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("request","quantity", item.getQuantity(),false, new String[]{"max.request.quantity"},new Object[]{9999},null));
        }
        //Global Error
        if (item.getPrice() != null && item.getItemName() != null) {
            int totalPrice = item.getPrice( ) * item.getQuantity();
            if (totalPrice < 10000) {
                bindingResult.addError(new ObjectError("request",new String[]{"totalPriceMin"},new Object[]{10000,totalPrice},null));
            }
        }

        // if validation failed, show AddForm again

        // success logic
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
    /*@PostMapping("/add")*/
    public String addItemV4(@ModelAttribute Item item,BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        //validation logic, Field Error
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.rejectValue("itemName","required");
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 10000000) {
            bindingResult.rejectValue("price","range",new Object[]{1000,100000}, null);
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.rejectValue("quantity","max",new Object[]{9999},null);
        }
        //Global Error
        if (item.getPrice() != null && item.getItemName() != null) {
            int totalPrice = item.getPrice( ) * item.getQuantity();
            if (totalPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000,totalPrice},null);
            }
        }

        // if validation failed, show AddForm again
        if (bindingResult.hasErrors()) {
            log.info("BindingResult Errors = {}", bindingResult);
            return "validation/v2/addForm";
        }
        // success logic
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
    /*@PostMapping("/add")*/
    public String addItemV5(@ModelAttribute Item item,BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        //validation logic, Field Error
        itemValidator.validate(item, bindingResult);

        // if validation failed, show AddForm again
        if (bindingResult.hasErrors()) {
            log.info("BindingResult Errors = {}", bindingResult);
            return "validation/v2/addForm";
        }
        // success logic
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
    @PostMapping("/add")
    public String addItemV6(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // if validation failed, show AddForm again
        if (bindingResult.hasErrors()) {
            log.info("BindingResult Errors = {}", bindingResult);
            return "validation/v2/addForm";
        }
        // success logic
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }




    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

