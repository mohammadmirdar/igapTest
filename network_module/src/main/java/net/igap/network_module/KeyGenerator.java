package net.igap.network_module;

import android.util.Base64;

import net.iga.common.HelperString;
import net.iga.common.KeyStore;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class KeyGenerator {
    private int ivSize;
    private String symmetricMethod;
    private String symmetricKeyString;
    private SecretKeySpec symmetricKey;

    private static final KeyGenerator[] instance = new KeyGenerator[3];
    private String TAG = getClass().getSimpleName() + " ";

    public static KeyGenerator getInstance(int account) {
        KeyGenerator localInstance = instance[account];
        if (localInstance == null) {
            synchronized (KeyGenerator.class) {
                localInstance = instance[account];
                if (localInstance == null) {
                    instance[account] = localInstance = new KeyGenerator();
                }
            }
        }
        return localInstance;
    }

    public void initKey(int keyLength) {
        try {
            symmetricKeyString = HelperString.generateKey(keyLength);
            symmetricKey = HelperString.generateSymmetricKey(symmetricKeyString);
        } catch (Exception e) {
           // FileLog.e(e);
        }
    }

    public SecretKeySpec getSymmetricKey() {
        return symmetricKey;
    }

    public void resetKeys() {
        symmetricKey = null;
        symmetricKeyString = null;
    }

    public Cipher getUploadCipher(byte[] iv) {
        try {
            Cipher uploadCipher = Cipher.getInstance("AES_256/CBC/PKCS5Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKey key2 = new SecretKeySpec(symmetricKeyString.getBytes(), "AES");
            uploadCipher.init(Cipher.ENCRYPT_MODE, key2, ivSpec);
            return uploadCipher;
        } catch (Exception e) {
           // FileLog.e(e);
        }

        return null;
    }

    public Cipher getDownloadCipher(byte[] iv) {
        try {
            Cipher downloadCipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            downloadCipher.init(Cipher.DECRYPT_MODE, symmetricKey, ivSpec);
            return downloadCipher;
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException e) {
            //FileLog.e(e);
        }

        return null;
    }

    public byte[] getEncryptedKey(String publicKey, int secondaryChunkSize) {
        try {
            RSAPublicKey rsaPublicKeyServer = (RSAPublicKey) HelperString.getPublicKeyFromPemFormat(publicKey);
            PublicKey pubKeyServer = KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(rsaPublicKeyServer.getModulus(), rsaPublicKeyServer.getPublicExponent()));

            RSAPublicKey rsaPublicKeyClient = (RSAPublicKey) HelperString.getPublicKeyFromPemFormat(KeyStore.PUBLIC_KEY_CLIENT);
            PublicKey pubKeyClient = KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(rsaPublicKeyClient.getModulus(), rsaPublicKeyClient.getPublicExponent()));

            return AESCrypt.encryptSymmetricKey(pubKeyServer, pubKeyClient, symmetricKey.getEncoded(), secondaryChunkSize);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            //FileLog.e(e);
        }

        return null;
    }

    public String encryptGiftCardDetail(String data) {
        try {
            byte[] binary = Base64.decode(data, Base64.DEFAULT);
            byte[] iv = Arrays.copyOfRange(binary, 0, ivSize);
            byte[] message = Arrays.copyOfRange(binary, ivSize, binary.length);
            byte[] encryptedBytes = AESCrypt.decrypt(symmetricKey, iv, message);
            return new String(encryptedBytes);
        } catch (GeneralSecurityException e) {
           // FileLog.e(e);
        }

        return null;
    }

    public String encryptMobileBank(byte[] auth) throws GeneralSecurityException {
        byte[] encryptedBytes = AESCrypt.encrypt(symmetricKey, auth);
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    public void securingDone(int symmetricIvSize, String method) {
        ivSize = symmetricIvSize;
        symmetricMethod = method.split("-")[2];
    }

    public int getIvSize() {
        return ivSize;
    }

    public boolean needSecure() {
        return symmetricKey == null;
    }

    public String getSymmetricMethod() {
        return symmetricMethod;
    }
}
