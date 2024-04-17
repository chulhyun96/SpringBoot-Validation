#### 오류 코드 관리 전략

오류 코드 관리전략의 핵심은 **구체적인 것에서 덜 구체적인 것으로**이다.
`MessageCodesResovler`는 `required.item.itemName`처럼 구체적인 것을 먼저 만들어주고, `required`처럼 덜 구체적인것을 가장 나중에 만든다.

이렇게 하면 메시지와 관련된 공통 전략을 편리하게 도입할 수 있기 때문이다.

**왜 이렇게 복잡하게 사용하는가**
모든 오류 코드에 대해서 메시지를 각각 다 정의하면 개발자 입장에서는 관리하기가 너무 힘들다. 그렇기 때문에 크게 중요하지 않은 메시지는 범용성 있는 `required`같은 메시지로 끝내고, 정말 중요한 메시지는 꼭 필요할 때 구체적으로 적어서 사용하는 방식이 더 효과적이다.


**이제 이러한 오류 코드 전략을 도입해보자**
`errors.properties`
```properties
#required.item.itemName=상품 이름은 필수입니다. #range.item.price=가격은 {0} ~ {1} 까지 허용합니다. #max.item.quantity=수량은 최대 {0} 까지 허용합니다. #totalPriceMin=가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}

#==ObjectError==  
#Level1  
totalPriceMin.item=상품의 가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}

#Level2 - 생략  
totalPriceMin=전체 가격은 {0}원 이상이어야 합니다. 현재 값 = {1}

#==FieldError==  
#Level1  
required.item.itemName=상품 이름은 필수입니다.
range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
max.item.quantity=수량은 최대 {0} 까지 허용합니다.

#Level2 - 생략

#Level3  
required.java.lang.String = 필수 문자입니다. 
required.java.lang.Integer = 필수 숫자입니다. 
min.java.lang.String = {0} 이상의 문자를 입력해주세요. 
min.java.lang.Integer = {0} 이상의 숫자를 입력해주세요.
range.java.lang.String = {0} ~ {1} 까지의 문자를 입력해주세요. 
range.java.lang.Integer = {0} ~ {1} 까지의 숫자를 입력해주세요. 
max.java.lang.String = {0} 까지의 문자를 허용합니다.

max.java.lang.Integer = {0} 까지의 숫자를 허용합니다.

#Level4

required = 필수 값 입니다.

min= {0} 이상이어야 합니다.

range= {0} ~ {1} 범위를 허용합니다.

max= {0} 까지 허용합니다.
```

크게 객체 오류와 필드 오류를 나누었다. 그리고 범용성에 따라 레벨을 나누어두었다.
그리고 이렇게 생성된 메시지 코드를 기반으로 순서대로 `MessageSource` 에서 메시지에서 찾는다.

구체적인 것에서 덜 구체적인 순서대로 찾는다. 메시지에 1번이 없으면 2번을 찾고, 2번이 없으면 3번을 찾는다. 이렇게 되면 만약에 크게 중요하지 않은 오류 메시지는 기존에 정의된 것을 그냥 **재활용** 하면 된다!

**정리**
1. `rejectValue()` 호출
2. `MessageCodesResolver` 를 사용해서 검증 오류 코드로 메시지 코드들을 생성
3. `new FieldError()` 를 생성하면서 메시지 코드들을 보관
4. `th:erros` 에서 메시지 코드들로 메시지를 순서대로 메시지에서 찾고, 출력한다.

 

**이렇게 한 이유**

   에러 메시지 내용이 바뀐다거나, 나중에 유지보수를 한다고 했을 때 일일이 찾아서 해야하는 번거러움을 한번에 없애준다. errors.properties 파일을 만들고, error 메시지를 정의한 후 BindingResult를 이용하여, validation 로직을 정의해줌으로써, error 메시지를 한곳에서 정리하고 정의할 수 있다는 것에 **의의**를 둘 것.
