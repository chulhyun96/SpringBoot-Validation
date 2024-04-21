package hello.itemservice.web.validation.api;

import hello.itemservice.web.validation.form.ItemSaveRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/validation/api/items")
public class ValidationItemApiController {

    @PostMapping("/add")
    public Object addItem(@RequestBody @Validated ItemSaveRequest request, BindingResult bindingResult) {
        log.info("API 컨트롤러 호출");
        if (bindingResult.hasErrors()) {
            log.info("error validation : {}", bindingResult);
            return bindingResult.getAllErrors();
        }
        log.info("성공");
        return request;
    }
}
