 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.entities.util;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import ois.cc.gravity.AppProps;

/**
 * @author Prakasha.prusty
 * 5 Aug, 2024
 */
public class CipherUtil
{
    public static String Encrypt(String apikey) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        //SecretKeyFactory factory = SecretKeyFactory.getInstance("AES");
        SecretKey key = new SecretKeySpec(AppProps.SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");

        // Create a Cipher instance for encryption and decryption
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(apikey.getBytes(StandardCharsets.UTF_8));

        // Encode the encrypted message using Base64
        String encodedMessage = Base64.getEncoder().encodeToString(encryptedBytes);

        return encodedMessage;

    }

    public static String Decrypt(String encodedkey) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
//        SecretKeyFactory factory = SecretKeyFactory.getInstance("AES");
        SecretKey key = new SecretKeySpec(AppProps.SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");

        // Create a Cipher instance for encryption and decryption
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

        // Decode the message
        byte[] decodedBytes = Base64.getDecoder().decode(encodedkey);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);

    }
}
