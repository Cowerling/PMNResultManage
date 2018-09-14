package com.cowerling.pmn.web.exception;

import com.cowerling.pmn.annotation.ToResourceNotFound;
import com.cowerling.pmn.exception.DataUploadException;
import com.cowerling.pmn.exception.EncoderServiceException;
import com.cowerling.pmn.exception.ResourceNotFoundException;
import com.cowerling.pmn.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class WideExceptionHandler {
    private boolean fromSpecificAnnotation(Throwable throwable, Class<? extends Annotation> annotationClass) {
        try {
            List<StackTraceElement> stackTraceElements = Arrays.stream(throwable.getStackTrace()).filter(x -> x.getClassName().contains(WideExceptionHandler.class.getPackage().getName())).collect(Collectors.toList());
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                Method method = Arrays
                        .stream(Class
                                .forName(stackTraceElement.getClassName())
                                .getSuperclass()
                                .getMethods()).filter(x -> x.getName().equals(stackTraceElement.getMethodName())).findAny().get();

                if (method.isAnnotationPresent(annotationClass)) {
                    return true;
                }
            }
        } catch (ClassNotFoundException e) {
            return false;
        }

        return false;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String resourceNotFoundHandler() {
        return "error/resource";
    }

    @ExceptionHandler(EncoderServiceException.class)
    public String encoderServiceHandler() { return "error/internal"; }

    @ExceptionHandler(AuthenticationException.class)
    public String authenticationHandler(AuthenticationException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "error/authentication";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String accessDeniedHandler(AccessDeniedException e, Model model) {
        if (fromSpecificAnnotation(e, ToResourceNotFound.class)) {
            return "error/resource";
        } else {
            model.addAttribute("message", e.getMessage());
            return "error/authentication";
        }
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String userNotFoundHandler() {
        return "error/resource";
    }

    @ExceptionHandler(ParseException.class)
    public String parseHandler() {
        return "error/resource";
    }

    @ExceptionHandler(IOException.class)
    public String ioHandler() {
        return "error/resource";
    }

    @ExceptionHandler(DataUploadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    Map<String, String> dataUploadHandler(DataUploadException e) {
        return new HashMap<>() {
            {
                put("message", e.getMessage());
            }
        };
    }
}
