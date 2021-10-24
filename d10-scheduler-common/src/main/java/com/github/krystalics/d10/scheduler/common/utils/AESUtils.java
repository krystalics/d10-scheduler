package com.github.krystalics.d10.scheduler.common.utils;

import com.github.krystalics.d10.scheduler.common.CommonConstants;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;

/**
 * aes相关
 */
public class AESUtils {

    private static final Logger logger = LoggerFactory.getLogger(AESUtils.class);

    private static final String MODEL = "AES/ECB/PKCS7Padding";

    private static final String AES = "AES";

    private static final String BC = "BC";


    static {
        if (Security.getProvider(BC) == null) {
            Security.addProvider(new BouncyCastleProvider());
        } else {
            Security.removeProvider(BC);
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * ECB 分组密码模式 PKCS7Padding填充模式 不需要加密混淆向量
     *
     * @param content
     * @param key
     * @return
     */
    public static String encrypt(String content, String key) {
        try {
            byte[] bytes = Base64.decodeBase64(key.getBytes(CommonConstants.DEFAULT_CHARSET));
            SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, AES);
            Cipher cipher = Cipher.getInstance(MODEL, BC);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(content.getBytes(CommonConstants.DEFAULT_CHARSET));
            //使用base64做转码功能，相当于二次加密
            return new String(Base64.encodeBase64(encrypted));
        } catch (Exception e) {
            logger.error("加密过程中出错!content:{},key:{}", content, key, e);
        }
        return "";
    }

    /**
     * ECB 分组密码模式 PKCS7Padding填充模式 不需要加密混淆向量
     *
     * @param content
     * @param key
     * @return
     */
    public static String decrypt(String content, String key) {
        try {
            byte[] bytes = Base64.decodeBase64(key.getBytes(CommonConstants.DEFAULT_CHARSET));
            SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, AES);
            Cipher cipher = Cipher.getInstance(MODEL, BC);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decodeContent = Base64.decodeBase64(content.getBytes(CommonConstants.DEFAULT_CHARSET));
            byte[] decrypted = cipher.doFinal(decodeContent);
            return new String(decrypted, CommonConstants.DEFAULT_CHARSET);
        } catch (Exception e) {
            logger.error("解密过程中出错!content:{},key:{}", content, key, e);
        }
        return "";
    }

}
