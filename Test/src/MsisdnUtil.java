

public class MsisdnUtil {

    public static final String PRIVATE_KEY = "a20s21pi05r12e54";

    public static String encrypt(String msisdn,String timeStamp, String aesKey) {
        return AESEncryptUtil.encryptECB(msisdn + timeStamp, aesKey);
    }
    public static String decrypt(String encryptData, String aesKey)  {
        try {
            System.out.println("encryptData and aesKey:" + encryptData + "," + aesKey);
            String msisdn = AESEncryptUtil.decryptECB(encryptData, aesKey);
            return msisdn.substring(0, 11);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }


    public static void main(String[] args) {

        String s1 = encrypt("13670021203","", PRIVATE_KEY);

        String s2 = decrypt(s1, PRIVATE_KEY);

        System.out.println(s1);
        System.out.println(s2);

    }
}
