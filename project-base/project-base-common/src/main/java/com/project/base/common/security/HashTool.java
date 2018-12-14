package com.project.base.common.security;

import com.project.base.common.enums.EnumEncodeFormat;
import com.project.base.common.lang.string.EncodeTool;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashTool {

    private static final Logger logger = LoggerFactory.getLogger(HashTool.class);

    public static String sha256(String input, EnumEncodeFormat returnEncodeFormat) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(input.getBytes());
            return encode(bytes, returnEncodeFormat);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String hmacSha256(String input, String secretKey, EnumEncodeFormat returnEncodeFormat) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(input.getBytes());
            return encode(bytes, returnEncodeFormat);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    private static String encode(byte[] byteArray, EnumEncodeFormat returnEncodeFormat) {
        if (returnEncodeFormat.equals(EnumEncodeFormat.Hex))
            return EncodeTool.encodeHex(byteArray);
        else if (returnEncodeFormat.equals(EnumEncodeFormat.Base64))
            return EncodeTool.encodeBase64(byteArray);

        return null;
    }

    public static String sha1(String message) {
        return DigestUtils.sha1Hex(message);
    }
}
