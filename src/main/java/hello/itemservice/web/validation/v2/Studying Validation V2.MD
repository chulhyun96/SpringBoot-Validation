###  BindingResult

스프링이 제공하는 **BindingResult**를 사용하여 검증 오류 처리하기.

**BindingResult**
스프링이 제공하는기능으로, 검증 오류에 대한 결과를 바인딩(묶어서 보관하는) 객체이다.
BindingResult가 있으면 @ModelAttribute에 데이터 바인딩 시 오류가 발생해도 컨트롤러가 호출된다.
```java

@PostMapping("/add")  
public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {  
  
  
    //validation logic  
    if (!StringUtils.hasText(item.getItemName())) {  
        bindingResult.addError(new FieldError("item","itemName","상품 이름은 필수 입니다."));  
    }  
    if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 10000000) {  
        bindingResult.addError(new FieldError("item","price","ERROR Price."));  
    }  
    if (item.getQuantity() == null || item.getQuantity() >= 9999) {  
        bindingResult.addError(new FieldError("item","quantity","ERROR Quantity."));  
    }  
    if (item.getPrice() != null && item.getItemName() != null) {  
        int totalPrice = item.getPrice() * item.getQuantity();  
        if (totalPrice < 10000) {  
            bindingResult.addError(new ObjectError("item","ERROR Price * Quantity."));  
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
```
**주의**  
`BindingResult bindingResult` 파라미터의 위치는 `@ModelAttribute Item item` 다음에 와야 한다.

**FieldError 생성자 요약**
```java
public FieldError(String objectName, String field, String defaultMessage) {} 
```

필드에 오류가 있으면 `FieldError` 객체를 생성해서 `bindingResult` 에 담아두면 된다. `objectName` : `@ModelAttribute` 이름  
`field` : 오류가 발생한 필드 이름  
`defaultMessage` : 오류 기본 메시지

**글로벌 오류 - ObjectError**
```java
bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
```

**ObjectError 생성자 요약** ```
-  public ObjectError(String objectName, String defaultMessage) {}
- 특정 필드를 넘어서는 오류가 있으면 `ObjectError` 객체를 생성해서 `bindingResult` 에 담아두면 된다.
- `objectName` : `@ModelAttribute` 의 이름  
  `defaultMessage` : 오류 기본 메시지


```html
    <form action="item.html" th:action th:object="${item}" method="post">
        <div th:if="${#fields.hasGlobalErrors()}">
            <p class="field-error" th:each="err : ${#fields.globalErrors()}" th:text="${err}">전체 오류 메시지</p>
        </div>
        <div>
            <label for="itemName" th:text="#{label.item.itemName}">상품명</label>
            <input type="text" id="itemName" th:field="*{itemName}" class="form-control" placeholder="이름을 입력하세요"
            th:errorclass="field-error">
            <div class="field-error" th:errors="*{itemName}"></div>
        </div>
```

**타임리프 스프링 검증 오류 통합 기능**  
타임리프는 스프링의 `BindingResult` 를 활용해서 편리하게 검증 오류를 표현하는 기능을 제공한다.
- `#fields` : `#fields` 로 `BindingResult` 가 제공하는 검증 오류에 접근할 수 있다.
- `th:errors` : 해당 필드에 오류가 있는 경우에 태그를 출력한다. `th:if` 의 편의 버전이다.
- `th:errorclass` : `th:field` 에서 지정한 필드에 오류가 있으면 `class` 정보를 추가한다

**글로벌 오류 처리**
```html
<div th:if="${#fields.hasGlobalErrors()}">
	<p class="field-error" th:each="err : ${#fields.globalErrors()}" th:text="$ {err}">전체 오류 메시지</p>
</div>
```

**필드 오류 처리**
```html
<input type="tex" id="itemName" th:field="*{itemName}" th:errorclass="field-error" class="form-control" placeholder="이름을 입력하세요">  
<div class="field-error" th:errors="*{itemName}">
상품명 오류</div> 
```

`BindingResult` 는 Model에 자동으로 포함된다.

### 추가

**예를 들어 @ModelAttribute에 바인딩 시 타입 오류가 발생하게 된다면**
-> `BindingResult`가 없으면 400오류가 나오며 컨트롤러 자체가 호출되지 않지만, `BindingResult`가 있으면 오류 정보를`400Erroor`를 `FieldError`에 담아서 컨트롤러를 정상 호출한다.

**BindingResult에 검증 오류를 적용하는 3가지 방법**
- @ModelAttribute의 객체에 타입 오류등으로 바인딩이 실패하는 경우 스프링이 `fieldError`를 생성해서 `BindingResult`에 넣어준다.
- 개발자가 직접 넣어준다. (==위의 예제 코드와 같은 방식들==)
- `Validator`를 사용한다.

**정리**  
현재 `BindingResult` , `FieldError` , `ObjectError` 를 사용해서 오류 메시지를 처리하는 방법을 알아보았다. 그런데 오류가 발생하는 경우 고객이 입력한 내용이 모두 사라진다. 이 문제를 해결해보자.





