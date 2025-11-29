package com.user.exception;

import com.user.util.message.UserMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ApiExceptionHandlerTest {

    @InjectMocks
    private ApiExceptionHandler handler;

    @Test
    void testHandleEmail() {
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("Email exists");
        ResponseEntity<Map<String, String>> response = handler.handleEmail(ex);

        assertEquals(409, response.getStatusCodeValue());
        assertEquals("Email exists", response.getBody().get(UserMessage.MESSAGE));
    }

    @Test
    void testHandleBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Bad argument");
        ResponseEntity<Map<String, String>> response = handler.handleBadRequest(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Bad argument", response.getBody().get(UserMessage.MESSAGE));
    }

    @Test
    void testHandleValidation() {
        FieldError fieldError = new FieldError("objectName", "field", "must not be blank");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidation(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().get(UserMessage.MESSAGE).contains("field: must not be blank"));
    }

    @Test
    void testHandleNotFound() {
        NoSuchElementException ex = new NoSuchElementException("Not found");
        ResponseEntity<Map<String, String>> response = handler.handleNotFound(ex);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Not found", response.getBody().get(UserMessage.MESSAGE));
    }

    @Test
    void testHandleAll() {
        Exception ex = new Exception("Something went wrong");
        ResponseEntity<Map<String, String>> response = handler.handleAll(ex);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals(UserMessage.INTERNAL_SERVER_ERROR, response.getBody().get(UserMessage.MESSAGE));
    }
}