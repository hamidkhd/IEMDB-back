package ir.ac.ut.ie.Utilities;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashCreator {
    private static HashCreator instance;

    public static HashCreator getInstance() {
        if (instance == null)
            instance = new HashCreator();
        return instance;
    }

    public String getMD5Hash(String plaintext) throws NoSuchAlgorithmException {
        String hashText = null;

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(plaintext.getBytes());
        hashText = convertToHex(messageDigest);

        return hashText;
    }

    private static String convertToHex(final byte[] messageDigest) {
        BigInteger bigint = new BigInteger(1, messageDigest);
        String hexText = bigint.toString(16);
        while (hexText.length() < 32) {
            hexText = "0".concat(hexText);
        }
        return hexText;
    }
}
