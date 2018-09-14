package com.cowerling.pmn.utils;

import org.apache.commons.text.RandomStringGenerator;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

public class StringUtils {
    public static String random() {
        RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder().withinRange('0', 'z').filteredBy(LETTERS, DIGITS).build();
        return randomStringGenerator.generate(20);
    }
}
