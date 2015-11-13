package fxchat.helpers;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//DEBUG
//import java.math.BigInteger;
/**
 * Created by rickwhalley on 12/11/2015.
 * HashPassword - Singleton helper to hash passwords
 */
public class HashPassword {

    public final static HashPassword instance = new HashPassword();

    private HashPassword() {
    }

    public static HashPassword getInstance() {
        return instance;
    }

    /*
        Hash pword with SHA-1 <- not cryptographically secure but still > MD5
        See http://csrc.nist.gov/groups/ST/hash/statement.html for details
    */
    public byte[] hashPassword(String pword) throws NoSuchAlgorithmException {
        // Use SHA-1
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        // Shouldn't fail unless oracle change something.
        try {
            md.update(pword.getBytes("UTF8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // DEBUG: Convert Byte[] to hexadecimal string for use with online testers
        // WARNING: NEVER use REAL passwords on this system or with online testers
        // System.out.println(String.format("%040x", new BigInteger(1, md.digest())));
        return md.digest();
    }

}