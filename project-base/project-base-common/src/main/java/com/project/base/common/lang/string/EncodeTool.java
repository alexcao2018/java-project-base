package com.project.base.common.lang.string;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class EncodeTool {

    private static final String DEFAULT_ENCODING = "UTF-8";

    public static String encodeHex(byte[] input) {
        return Hex.encodeHexString(input);
    }

    public static byte[] decodeHex(String input) {
        try {
            return Hex.decodeHex(input.toCharArray());
        } catch (DecoderException e) {
            return null;
        }
    }

    public static String encodeBase64(byte[] input) {
        return Base64.encodeBase64String(input);
    }

    public static byte[] decodeBase64(String input) {
        return Base64.decodeBase64(input);
    }


    public static String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String urlDecode(String input) {
        try {
            return URLDecoder.decode(input, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }




}
