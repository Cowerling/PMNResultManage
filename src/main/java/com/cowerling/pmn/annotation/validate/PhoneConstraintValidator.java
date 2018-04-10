package com.cowerling.pmn.annotation.validate;

import com.cowerling.pmn.annotation.Phone;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneConstraintValidator implements ConstraintValidator<Phone, String> {
    @Override
    public void initialize(Phone phone) {}

    @Override
    public boolean isValid(String phoneField, ConstraintValidatorContext constraintValidatorContext) {
        if (phoneField == null) {
            return false;
        }

        return phoneField.matches("^\\d{11}$");
    }
}
