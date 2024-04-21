
package hello.itemservice.web.validation.v4;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.web.validation.form.ItemSaveRequest;
import hello.itemservice.web.validation.form.ItemUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v4/items")
@RequiredArgsConstructor
public class ValidationItemControllerV4 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v4/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v4/addForm";
    }

    @PostMapping("/add")
    public String addItemV6(@Validated @ModelAttribute("item") ItemSaveRequest request, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.info("validation v3 FieldErrors : {}", bindingResult);
            return "validation/v4/addForm";
        }
        if (request.getPrice() * request.getQuantity() < 10000) {
            log.info("validation v3 ObjectErrors : {}", bindingResult);
            bindingResult.reject("totalPriceMin", new Object[]{10000}, null);
            return "validation/v4/addForm";
        }
        // success logic
        Item newItem = Item.from(request);
        Item savedItem = itemRepository.save(newItem);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        // "redirect:/validation/v4/items/{itemId}"; itemId를 사용하기 위해서
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v4/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("request", item);
        return "validation/v4/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @Validated @ModelAttribute("request") ItemUpdateRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "validation/v4/editForm";
        }
        if (request.getPrice() * request.getQuantity() < 10000) {
            bindingResult.reject("totalPriceMin", new Object[]{10000}, null);
            return "validation/v4/editForm";
        }
        Item updateItem = new Item();
        updateItem.setItemName(request.getItemName());
        updateItem.setQuantity(request.getQuantity());
        updateItem.setPrice(request.getPrice());
        itemRepository.update(itemId, updateItem);
        return "redirect:/validation/v4/items/{itemId}";
    }

}

