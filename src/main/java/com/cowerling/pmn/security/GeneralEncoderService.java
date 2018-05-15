package com.cowerling.pmn.security;

import com.cowerling.pmn.exception.EncoderServiceException;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GeneralEncoderService {
    private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    private static final String ALGORITHM = "AES";

    private String keySeed() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");
        String time = simpleDateFormat.format(new Date());
        return Base64.getEncoder().encodeToString(time.getBytes()).replace("/","").replace("+","");
    }

    private Key key() throws EncoderServiceException {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
            secureRandom.setSeed(keySeed().getBytes());
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(secureRandom);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            throw new EncoderServiceException();
        }
    }

    public String encrypt(String content) throws EncoderServiceException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key());
            byte[] result = cipher.doFinal(content.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(result)
                    .replace('/', '_')
                    .replace('+', '-');
        } catch (Exception e) {
            throw new EncoderServiceException();
        }
    }

    public String decrypt(String content) throws EncoderServiceException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key());
            byte[] result = cipher.doFinal(Base64.getDecoder().decode(content.replace('_', '/').replace('-', '+')));
            return new String(result, "UTF-8");
        } catch (Exception e) {
            throw new EncoderServiceException();
        }
    }
}
