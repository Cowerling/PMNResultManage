package com.cowerling.pmn.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = ExceptionMessage.DATA_AUTHORITY_MODIFY)
public class DataAuthorityEditException extends Exception {
}
