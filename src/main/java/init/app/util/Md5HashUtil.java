package init.app.util;

import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by bojan.stankovic@codetri.be on 6/8/18.
 */
public class Md5HashUtil {

    public static String hashPassword(String password) {

        try {
            MessageDigest mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(password.getBytes(), 0, password.length());
            String passwordHash = new BigInteger(1, mdEnc.digest()).toString(16);

            return passwordHash;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Boolean matchesMd5PasswordHash(String rawPassword, String md5EncodedPassword) {

        if (!StringUtils.hasText(rawPassword) || !StringUtils.hasText(md5EncodedPassword)) {
            return false;
        }

        return md5EncodedPassword.equals(hashPassword(rawPassword));

    }

}
