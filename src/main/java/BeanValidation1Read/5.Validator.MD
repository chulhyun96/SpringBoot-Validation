### Validator 분리

현재 컨트롤러에 있는 검증 로직이다.

복잡하고 가독성이 떨어지고, 메인로직인 상품 등록 성공에 대한 로직은 3줄이면 끝나는거에 비해 검증로직은 너무 많다.
나눌 필요가 있다.


```java
 @PostMapping("/add")
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
```

**목표**
- 복잡한 검증 로직을 별도로 분리하자.

컨트롤러에서 검증 로직이 차지하는 부분은 매우 크다. 이런 경우 별도의 클래스로 역할을 분리하는 것이 좋다. 그리고

이렇게 분리한 검증 로직을 재사용 할 수도 있다.

스프링은 검증을 체계적으로 제공하기 위해 다음 인터페이스를 제공한다.

```java
public interface Validator {
     boolean supports(Class<?> clazz);
     void validate(Object target, Errors errors);
}
```

```java
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
```
ItemValidator 클래스를 만들고 Validator 인터페이스를 상속받아서 메서드를 구현했다.

`supports` 메서드의 isAssignableFrom(aClass), 그리고 `validate` 메서드는 파라미터로 인자 Object와 Errors를 받는다. 이 경우에 Object로 넘어온 객체는 Item이 되고, Errors는 BindingResult의 부모 클래스이다. 그러니 BindingResult 타입을 받을 수 있다.

해당 메서드의 로직은 원래 컨트롤러에 있던 검증 로직이였다. 그것들을 모두 `validate` 메서드에 옮겼다.

    @PostMapping("/add")
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

로직을 분리한 후의 컨트롤러 메서드이다. 엄청 깔끔해졌다.

### WebDataBinder 사용하기
`WebDataBinder`는 스프링의 파라미터 바인딩의 역할을 해주고 검증 기능도 내부에 포함한다. 그리고 Validator를 가지고 검증도 Spring 내부에서 해준다.


```java
    @InitBinder
    public void init(WebDataBinder dataBinder) {
        dataBinder.addValidators(itemValidator);
    }
```
`@InitBinder` 애노테이션을 사용하여 `WebDataBinder`를 매개변수로 넣어준다.
그 후 `addValidators`를 호출하여 Validator를 구현한 구현체를 인자로 넘겨주면 끝난다.

그리고 `@InitBinder` 해당 애노테이션은 해당 컨트롤러에만 영향을 준다. 해당 메서드는 해당 컨트롤러의 `RequestMapping`이 호출 될 때마다 호출된다.

validator를 직접 호출하는 부분이 사라지고, 대신에 검증 대상 앞에 `@Validated` 가 붙었다.
```java
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
```
**동작 방식**

`@Validated` 는 검증기를 실행하라는 애노테이션이다.

이 애노테이션이 붙으면 앞서 `WebDataBinder` 에 등록한 검증기를 찾아서 실행한다. 그런데 여러 검증기를 등록한다면 그 중에 어떤 검증기가 실행되어야 할지 구분이 필요하다. 이때 `supports()` 가 사용된다. 여기서는`supports(Item.class)` 호출되고, 결과가 `true` 이므로 `ItemValidator` 의 `validate()` 가 호출된다.

이 모든 과정을 자동으로 해준다.

**참고**  
검증시 `@Validated` `@Valid` 둘다 사용가능하다.

`javax.validation.@Valid` 를 사용하려면 `build.gradle` 의존관계 추가가 필요하다. `implementation 'org.springframework.boot:spring-boot-starter-validation'` `@Validated` 는 스프링 전용 검증 애노테이션이고, `@Valid` 는 자바 표준 검증 애노테이션이다.





