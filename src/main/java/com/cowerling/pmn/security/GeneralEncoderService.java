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
    private static final String STATIC_KEY_SEED = "HGHbmjDK";

    private String keySeed() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");
        String time = simpleDateFormat.format(new Date());
        return Base64.getEncoder().encodeToString(time.getBytes()).replace("/","").replace("+","");
    }

    private Key key(String keySeed) throws EncoderServiceException {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
            secureRandom.setSeed(keySeed.getBytes());
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(secureRandom);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            throw new EncoderServiceException();
        }
    }

    private String baseEncrypt(String content, String keySeed)throws EncoderServiceException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key(keySeed));
            byte[] result = cipher.doFinal(content.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(result)
                    .replace('/', '_')
                    .replace('+', '-');
        } catch (Exception e) {
            throw new EncoderServiceException();
        }
    }

    private String baseDecrypt(String content, String keySeed) throws EncoderServiceException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key(keySeed));
            byte[] result = cipher.doFinal(Base64.getDecoder().decode(content.replace('_', '/').replace('-', '+')));
            return new String(result, "UTF-8");
        } catch (Exception e) {
            throw new EncoderServiceException();
        }
    }

    public String encrypt(String content) throws EncoderServiceException {
        return baseEncrypt(content, keySeed());
    }

    public String staticEncrypt(String content) throws EncoderServiceException {
        return baseEncrypt(content, STATIC_KEY_SEED);
    }

    public String staticEncrypt(Long content) throws EncoderServiceException {
        return staticEncrypt(content.toString());
    }

    public String staticEncrypt(Integer content) throws EncoderServiceException {
        return staticEncrypt(content.toString());
    }

    public String decrypt(String content) throws EncoderServiceException {
        return baseDecrypt(content, keySeed());
    }

    public String staticDecrypt(String content) throws EncoderServiceException {
        return baseDecrypt(content, STATIC_KEY_SEED);
    }
}
