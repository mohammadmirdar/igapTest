package net.iga.common;

import android.text.format.DateUtils;

import javax.crypto.spec.SecretKeySpec;

public class KeyStore {
    public static int ivSize;
    public static SecretKeySpec symmetricKey;
    public static String symmetricMethod;
    public static final int LOOKUP_MAP_RESPONSE_OFFSET = 30000;
    public static final int TIME_OUT_DELAY_MS = (int) (DateUtils.SECOND_IN_MILLIS);
    public static final int TIME_OUT_MS = (int) (20 * DateUtils.SECOND_IN_MILLIS);
    public static boolean FILE_LOG_ENABLE;
    public static boolean isTimeWhole = false;
    public static long currentTime;
    public static long serverHeartBeatTiming = 60 * 1000;
    public static final String PUBLIC_KEY_CLIENT = "-----BEGIN PUBLIC KEY-----\n"
            + "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo+inlAfd8Qior8IMKaJ+\n"
            + "BREJcEc9J9RhHgh6g/LvHKsnMaiEbAL70jQBQTLpCRu5Cnpj20+isOi++Wtf/pIP\n"
            + "FdJbD/1H+5jS+ja0RA6unp93DnBuYZ2JjV60vF3Ynj6F4Vr1ts5Xg5dJlEaOcOO2\n"
            + "YzOU97ZGP0ozrXIT5S+Y0BC4M9ieQmlGREzt3UZlTBbyUYPS4mMFh88YcT3QTiTA\n"
            + "k897qlJLxkYxVyAgwAD/0ihmWEkBQe9IxwVT/x5/QbixGSl4Zvd+5d+9sTZcSZQS\n"
            + "iJInT4E6DcmgAVYu5jFMWJDTEuurOQZ1W4nbmGyoY1bZXaFoiMPfzy72VIddkoHg\n"
            + "mwIDAQAB\n"
            + "-----END PUBLIC KEY-----\n";


    public static final int ACCEPT = 1;
    public static final int REJECT = 0;
}
