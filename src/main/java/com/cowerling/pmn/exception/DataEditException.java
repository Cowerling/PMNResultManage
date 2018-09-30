package com.cowerling.pmn.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = ExceptionMessage.DATA_EDIT_SAVE)
public class DataEditException extends Exception {
}
