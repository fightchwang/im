package com.design.im.util;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;

public class Pbkdf2Sha256Util {
    private static final Logger logger= LoggerFactory.getLogger(Pbkdf2Sha256Util.class);
    //default 20000
    private static final Integer DEFAULT_ITERATIONS = 20000;
    //algorithm name
    private static final String algorithm = "pbkdf2_sha256";

    /**
     * get hashed password
     * @param password plain text
     * @param salt
     * @param iterations
     * @return
     */
    private static String getEncodedHash(String password, String salt, int iterations) {
        // Returns only the last part of whole encoded password
        SecretKeyFactory keyFactory = null;
        try {
            keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Could NOT retrieve PBKDF2WithHmacSHA256 algorithm",e);
        }
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(Charset.forName("UTF-8")), iterations, 256);
        SecretKey secret = null;
        try {
            secret = keyFactory.generateSecret(keySpec);
        } catch (InvalidKeySpecException e) {
            logger.error("Could NOT generate secret key",e);
        }
        byte[] rawHash = secret.getEncoded();
        byte[] hashBase64 = Base64.getEncoder().encode(rawHash);

        return new String(hashBase64);
    }
    /**
     * salt
     * @return String
     */
    private static String getsalt(){
        int length = 12;
        Random rand = new Random();
        char[] rs = new char[length];
        for(int i = 0; i < length; i++){
            int t = rand.nextInt(3);
            if (t == 0) {
                rs[i] = (char)(rand.nextInt(10)+48);
            } else if (t == 1) {
                rs[i] = (char)(rand.nextInt(26)+65);
            } else {
                rs[i] = (char)(rand.nextInt(26)+97);
            }
        }
        return new String(rs);
    }
    /**
     * rand salt
     * iterations is default 20000
     * @param password
     * @return
     */
    public static String encode(String password) {
        return encode(password, getsalt());
    }
    /**
     * rand salt
     * @param password
     * @return
     */
    public static String encode(String password,int iterations) {
        return encode(password, getsalt(),iterations);
    }
    /**
     * iterations is default 20000
     * @param password
     * @param salt
     * @return
     */
    public static String encode(String password, String salt) {
        return encode(password, salt, DEFAULT_ITERATIONS);
    }

    /**
     *
     * @param password plain password
     * @param salt salt
     * @param iterations
     * @return
     */
    public static String encode(String password, String salt, int iterations) {
        // returns hashed password, along with algorithm, number of iterations and salt
        String hash = getEncodedHash(password, salt, iterations);
        return String.format("%s$%d$%s$%s", algorithm, iterations, salt, hash);
    }

    /**
     * verify the hashed password
     * @param password plain password
     * @param hashedPassword
     * @return
     */
    public static boolean verification(String password, String hashedPassword) {
        // hashedPassword consist of: ALGORITHM, ITERATIONS_NUMBER, SALT and
        // HASH; parts are joined with dollar character ("$")
        String[] parts = hashedPassword.split("\\$");
        if (parts.length != 4) {
            // wrong hash format
            return false;
        }
        Integer iterations = Integer.parseInt(parts[1]);
        String salt = parts[2];
        String hash = encode(password, salt, iterations);
        return hash.equals(hashedPassword);
    }
}
