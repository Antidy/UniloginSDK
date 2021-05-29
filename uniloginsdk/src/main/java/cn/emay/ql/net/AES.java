package cn.emay.ql.net;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加解密工具【128位】
 *
 * @author Frank
 *
 */
public class AES {

    /**
     * 算法：AES/ECB/PKCS5Padding
     */
    public final static String ALGORITHM_AEPP = "AES/ECB/PKCS5Padding";

    public final static String PKCS7PADDING = "PKCS7Padding";

    /**
     * AES加密
     *
     * @param content
     *            内容
     * @param password
     *            密钥【128位】【16个英文或者数字】
     * @return 加密后数据
     */
    public static byte[] encrypt(String content, String password) {
        return encrypt(content, password, "UTF-8");
    }

    /**
     * AES加密
     *
     * @param content
     *            内容
     * @param password
     *            密钥【128位】【16个英文或者数字】
     * @param charset
     *            编码[UTF-8,GBK......]
     * @return 加密后数据
     */
    public static byte[] encrypt(String content, String password, String charset) {
        try {
            return encrypt(content.getBytes(charset), password.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * AES加密
     *
     * @param content
     *            内容
     * @param password
     *            密钥【128位】【16个byte】
     * @return 加密后数据
     */
    public static byte[] encrypt(byte[] content, byte[] password) {
        return encrypt(content, password, ALGORITHM_AEPP);
    }

    /**
     * AES解密
     *
     * @param content
     *            加密内容
     * @param password
     *            密钥【128位】【16个英文或者数字】
     * @return 解密后数据
     */
    public static byte[] decrypt(byte[] content, String password) {
        return decrypt(content, password, "UTF-8");
    }

    /**
     * AES解密
     *
     * @param content
     *            加密内容
     * @param password
     *            密钥【128位】【16个英文或者数字】
     * @param charset
     *            编码[UTF-8,GBK......]
     * @return 解密后数据
     */
    public static byte[] decrypt(byte[] content, String password, String charset) {
        try {
            return decrypt(content, password.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * AES解密
     *
     * @param content
     *            加密内容
     * @param password
     *            密钥【128位】【16个byte】
     * @return 解密后数据
     */
    public static byte[] decrypt(byte[] content, byte[] password) {
        return decrypt(content, password, ALGORITHM_AEPP);
    }

    /**
     * AES加密
     *
     * @param content
     *            内容
     * @param password
     *            密钥【128位】【16个byte】
     * @param algorithm
     *            算法
     * @return 加密后数据
     */
    public static byte[] encrypt(byte[] content, byte[] password, String algorithm) {
        if (content == null || password == null) {
            return null;
        }
        try {
            Cipher cipher = null;
            if (algorithm.endsWith(PKCS7PADDING)) {
                cipher = Cipher.getInstance(algorithm, "BC");
            } else {
                cipher = Cipher.getInstance(algorithm);
            }
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(password, "AES"));
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * AES解密
     *
     * @param content
     *            加密内容
     * @param password
     *            密钥【128位】【16个byte】
     * @param algorithm
     *            算法
     * @return 解密后数据
     */
    public static byte[] decrypt(byte[] content, byte[] password, String algorithm) {
        if (content == null || password == null) {
            return null;
        }
        try {
            Cipher cipher = null;
            if (algorithm.endsWith(PKCS7PADDING)) {
                cipher = Cipher.getInstance(algorithm, "BC");
            } else {
                cipher = Cipher.getInstance(algorithm);
            }
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(password, "AES"));
            byte[] bytes = cipher.doFinal(content);
            return bytes;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * AES加密
     *
     * @param content
     *            内容
     * @param password
     *            密钥【128位】【16个byte】
     * @param algorithm
     *            算法
     * @param ivStr
     *            向量【128位】【16个byte】
     * @return 加密后数据
     */
    public static byte[] encrypt(byte[] content, byte[] password, byte[] ivStr, String algorithm) {
        if (content == null || password == null) {
            return null;
        }
        try {
            Cipher cipher = null;
            if (algorithm.endsWith(PKCS7PADDING)) {
                cipher = Cipher.getInstance(algorithm, "BC");
            } else {
                cipher = Cipher.getInstance(algorithm);
            }
            IvParameterSpec iv = new IvParameterSpec(ivStr);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(password, "AES"), iv);
            return cipher.doFinal(content);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * AES解密
     *
     * @param content
     *            加密内容
     * @param password
     *            密钥【128位】【16个byte】
     * @param algorithm
     *            算法
     * @param ivStr
     *            向量【128位】【16个byte】
     * @return 解密后数据
     */
    public static byte[] decrypt(byte[] content, byte[] password, byte[] ivStr, String algorithm) {
        if (content == null || password == null) {
            return null;
        }
        try {
            Cipher cipher = null;
            if (algorithm.endsWith(PKCS7PADDING)) {
                cipher = Cipher.getInstance(algorithm, "BC");
            } else {
                cipher = Cipher.getInstance(algorithm);
            }
            IvParameterSpec iv = new IvParameterSpec(ivStr);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(password, "AES"), iv);
            byte[] bytes = cipher.doFinal(content);
            return bytes;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

}
