### FieldError, ObjectError

**목표**
- 사용자 입력 오류 메시지가 화면에 남도록 하자.
    -  예) 가격을 1000원 미만으로 설정시 입력한 값이 남아있어야 한다.
-  `FieldError` , `ObjectError` 에 대해서 더 자세히 알아보자.


**FieldError 생성자**
`fieldError`는 두 가지 생성자를 제공한다.

```java
public FieldError(String objectName, String field, String defaultMessage);
public FieldError(String objectName, String field, @Nullable Object
 rejectedValue, boolean bindingFailure, @Nullable String[] codes, @Nullable
 Object[] arguments, @Nullable String defaultMessage)
```


**파라미터 목록**
- `objectName` : 오류가 발생한 객체 이름
- `field` : 오류 필드
- `rejectedValue` : 사용자가 입력한 값(바인딩 시 거절된 값)
- `bindingFailure` : 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값
- `codes` : 메시지 코드
- `arguments` : 메시지에서 사용하는 인자
- `defaultMessage` : 기본 오류 메시지

`ObjectError` 도 유사하게 두 가지 생성자를 제공한다. 코드를 참고하자.


**오류 발생시 사용자 입력 값을 유지 할 수 있는 방법**
`new FieldError("item", "price", item.getPrice(), false, null, null, "가격은 1,000 ~1,000,000 까지 허용합니다.")``

```java
@Data
public class Item {

    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
```

사용자의 입력 데이터가 컨트롤러의 @ModelAttribute에 바인딩 되는 시점에 오류가 발생하면 모델 객체에 사용자의 입력값을 유지하기 어렵다.

예를 들어 가격에 숫자가 아닌 문자가 입력된다면 가격은 `Integer`타입이므로 문자를 보관할 수 있는 방법이 없다.

(==ModelAttribue==에 바인딩 되는 Item 객체의 필드에서 `Integer`타입인데, `String` 타입이 들어올 경우 받을 데이터 그릇이 없다.)

그래서 오류가 발생한 경우 사용자 입력값을 보관하는 별도의 방법이 필요하다. 그리고 이렇게 보관한 사용자 입력값을 검증 오류 발생시 화면에 다시 출력하면 된다.

**`FieldError`** 는 오류 발생시 사용자 입력 값을 저장하는 기능을 제공하는데, 그 기능을 제공하는 것이 바로 **`rejectedValue`** 가 바로 오류 발생시 사용자 입력값을 저장해주는 역할을 수행한다.

**`bindingFailure`**  는 타입 오류 같은 바인딩이 실패했는지 여부를 적어주면 되는데, 여기서는 바인딩이 실패한 것은 아니기 떄문에 false를 사용한다.

### 그럼 Error 발생 시 타임리프에서는 이걸 어떻게 알고 처리하는 것일까?

**타임리프의 사용자 입력 값 유지**
`th:field="*{}"` 타임리프의 `th:field="*{}"` 는 정상 상황에서는 모델 객체의 값을 사용하지만, 오류 발생시에는 `FieldError`에서 보관한 값을 사용해서 값을 출력한다.

**스프링의 바인딩 오류 처리**
타입 오류로 인한 바인딩 실패 시 스프링은 `FieldError`를 생성하면서 사용자가 입력한 값을 넣어둔다. 그리고 해당 오류를 `BindingResult`에 담아서 컨트롤러를 호출하는데, 따라서 타입 오류 같은 바인딩 실패시에도 사용자의 오류 메시지를 정상 출력할  수 있다.







