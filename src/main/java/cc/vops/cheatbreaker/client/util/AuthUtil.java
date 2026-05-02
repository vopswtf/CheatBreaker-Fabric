package cc.vops.cheatbreaker.client.util;

import java.security.PrivateKey;
import java.security.Signature;

public class AuthUtil {
    public static byte[] sign(byte[] data, PrivateKey privateKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(data);
        return sig.sign();
    }
}
