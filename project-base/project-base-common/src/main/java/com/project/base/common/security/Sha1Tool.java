package com.project.base.common.security;

import org.apache.commons.codec.digest.DigestUtils;

public class Sha1Tool {
    public static String encrypt(String message) {
        return DigestUtils.sha1Hex(message);
    }
}
