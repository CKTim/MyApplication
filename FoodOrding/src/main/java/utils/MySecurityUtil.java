package utils;

import java.security.MessageDigest;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


public class MySecurityUtil {
	//MD5����
	public static String string2MD5(String inStr){
        MessageDigest md5 = null;
        try{
            md5 = MessageDigest.getInstance("MD5");
        }catch (Exception e){
            e.printStackTrace();
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++){
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
	
	
	public static final String KEY_ALGORITHM = "DES";
    public static final String CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";
    private static final String key = "%&*(#$2urit!9506";

    private static SecretKey keyGenerator(String keyStr) throws Exception {
        byte input[] = HexString2Bytes(keyStr);
        DESKeySpec desKey = new DESKeySpec(input);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(desKey);
        return securekey;
    }

    private static int parse(char c) {
        if (c >= 'a') return (c - 'a' + 10) & 0x0f;
        if (c >= 'A') return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }

    //��ʮ�������ַ������ֽ�����ת�� 
    public static byte[] HexString2Bytes(String hexstr) {
        byte[] b = new byte[hexstr.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hexstr.charAt(j++);
            char c1 = hexstr.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }

    /** 
     * ����
     * @param data ����������
     * @return ���ܺ������ 
     */
    public static String encrypt(String data){
        Key deskey;
        Cipher cipher;
        byte[] results = null;
		try {
			deskey = keyGenerator(key);
			cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			SecureRandom random = new SecureRandom();
            cipher.init(Cipher.ENCRYPT_MODE, deskey, random);
			results = cipher.doFinal(data.getBytes("UTF-8"));
        } catch (Exception e) {
			e.printStackTrace();
		}
        return Base64.encodeToString(results, Base64.DEFAULT);
    }

    /** 
     * ���� 
     * @param data ����������
     * @param key ��Կ
     * @return ���ܺ������
     */
    public static String decrypt(String data){
    	Key deskey;
        Cipher cipher = null;
    	try{
    		deskey = keyGenerator(key);
    		cipher = Cipher.getInstance(CIPHER_ALGORITHM);
    		cipher.init(Cipher.DECRYPT_MODE, deskey);
    		return new String(cipher.doFinal(Base64.decode(data, Base64.DEFAULT)));
    	}catch (Exception e){
    		e.printStackTrace();
    	}
        return null;
    }
    //String to Base64
    public static String String2Base64(String data){
    	byte[] byteArray = data.getBytes();
    	return Base64.encodeToString(byteArray,Base64.DEFAULT);
    }
    //Base64 to String
    public static String Base64ToString(String data){
    	byte[] btyeArray = data.getBytes();
    	return new String(Base64.decode(btyeArray,Base64.DEFAULT));
    }
    
//    public static void main(String[] args) throws Exception {
//        String source = "{\"businessId\":\"2\",\"userId\":\"9\",\"sign\":\"321727fd61e716f553d2b601f77eed0a\"}";
//        System.out.println("ԭ��: " + source);
//        String key = "%&*(#$2urit!9506";
//        String encryptData = encrypt(source, key);
//        System.out.println("���ܺ�: " + encryptData);
//        String decryptData = String2Base64(encryptData);
//        System.out.println("base64��: " + decryptData);
//    }

}
