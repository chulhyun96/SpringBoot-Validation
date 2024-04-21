package hello.itemservice.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.ObjectError;

public class MessageCodesResolverTest {


    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodesResolverObject() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "request");
        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
        }
        Assertions.assertThat(codesResolver.resolveMessageCodes("required", "request")).isNotNull();
        Assertions.assertThat(messageCodes).containsExactly("required.request", "required");
    }

    @Test
    void messageCodesResolverField() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "request", "itemName", String.class);
        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
        }
        Assertions.assertThat(messageCodes).isNotNull();
        Assertions.assertThat(messageCodes).containsExactly("required.request.itemName", "required.itemName", "required.java.lang.String","required");
    }
}
