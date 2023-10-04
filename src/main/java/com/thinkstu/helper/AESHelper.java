package com.thinkstu.helper;

import org.bouncycastle.jce.provider.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.*;

public class AESHelper {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 金智教务系统的加密实现 Java
     *
     * @param password
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptAES(String password, String key) throws Exception {
        String randomString = randomString(RANDOM_STRING_LENGTH);
        String randomIv     = randomString(RANDOM_IV_LENGTH);
        // 金智的加密步骤
        // 1.随机的64位字符拼接在密码前面
        // 2.标准的AES-128-CBC加密
        // 3.将加密后的结果进行Base64编码
        // 4.随机iv并不影响加密和解密的结果，因此，固定或者随机都可以，但必须是16位
        return Base64Encrypt(AESEncrypt(randomString + password, key, randomIv));
    }


    private static final String CIPHER_NAME = "AES/CBC/PKCS5Padding";
    private static final String CHARSETNAME = "UTF-8";
    private static final String AES = "AES";
    private static final String BASE_STRING = "ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678";
    private static final int RANDOM_IV_LENGTH = 16;
    private static final int RANDOM_STRING_LENGTH = 64;

    /**
     * 作用：AES加密
     * @param data 待加密内容
     * @param key 加密密钥
     * @param iv   加密向量
     * @return 加密数据
     * @throws Exception
     */
    private static byte[] AESEncrypt(String data, String key, String iv) throws Exception {
        Cipher          cipher          = Cipher.getInstance(CIPHER_NAME);
        SecretKeySpec   secretKeySpec   = new SecretKeySpec(key.getBytes(CHARSETNAME), AES);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(CHARSETNAME));
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        return cipher.doFinal(data.getBytes(CHARSETNAME));
    }

    private static String Base64Encrypt(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * AES解密
     */
    private static String AESDecrypt(byte[] data, String key, String iv) throws Exception {
        Cipher          cipher          = Cipher.getInstance(CIPHER_NAME);
        SecretKeySpec   secretKeySpec   = new SecretKeySpec(key.getBytes(CHARSETNAME), AES);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(CHARSETNAME));
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        return new String(cipher.doFinal(data), CHARSETNAME);
    }

    /**
     * 获取随机字符
     *
     * @param bits
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static String randomString(int bits) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < bits; i++) {
            Random random = new Random();
            buffer.append(BASE_STRING.charAt(random.nextInt(BASE_STRING.length())));
        }
        return buffer.toString();
    }

}