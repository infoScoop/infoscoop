package org.infoscoop.util;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Crypt {

	private static final Crypt thisInstance = new Crypt();
	private static final long serialVersionUID = 7490854493720551678L;
	private static Log log = LogFactory.getLog(Crypt.class);
	public static final byte ENCRYPT = 0;
	public static final byte DECRYPT = 1;
	
	private static SecretKey secretKey;
	
	private Crypt() {
		DESKeySpec dk;
		try {
			dk = new DESKeySpec(new Long(serialVersionUID).toString().getBytes());
			SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");
		    secretKey = kf.generateSecret(dk);
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	public static Crypt gerCryptInstance() {
		return thisInstance;
	}
	
	public String doCrypt(byte mode, String str) throws Exception {
		if (Crypt.DECRYPT == mode) {
			return decryptByDES(str);
		} else if (Crypt.ENCRYPT == mode) {
			return encryptByDES(str);
		}
		return "";
	}
	
	private String encryptByDES(String str) throws Exception{
        Cipher c;
		try {
			c = Cipher.getInstance("DES/ECB/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = c.doFinal(str.getBytes());

            // convert into  hexadecimal number, and return as character string.
            String result = "";
            for (int i = 0; i < encrypted.length; i++) {
            	result += byte2HexStr(encrypted[i]);
            }
            
			return result;
		} catch (InvalidKeyException e) {
			log.error("The information of the private key may be broken.", e);
			throw e;
		} catch (IllegalBlockSizeException e) {
			log.error("The length of data is unjust.", e);
			throw e;
		}
	}
	
	private String decryptByDES(String str) throws Exception{
        Cipher c;
		
		try {
			byte[] tmp = new byte[str.length()/2];
            int index = 0;
            while (index < str.length()) {
            	// convert hexadecimal number into decimal number.
            	int num = Integer.parseInt(str.substring(index, index + 2), 16);
            	
            	// convert into signed byte.
            	if (num < 128) {
            		tmp[index/2] = new Byte(Integer.toString(num)).byteValue();
            	} else {
            		tmp[index/2] = new Byte(Integer.toString(((num^255)+1)*-1)).byteValue();
            	}
            	index += 2;
            }
            
            c = Cipher.getInstance("DES/ECB/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(c.doFinal(tmp));
		} catch (InvalidKeyException e) {
			log.error("The information of the private key may be broken.", e);
			throw e;
		} catch (IllegalBlockSizeException e) {
			log.error("he length of data is unjust.", e);
			throw e;
		}
	}
	
	private String byte2HexStr(byte binary) {
		StringBuffer sb= new StringBuffer();
		int hex;
		
		hex = (int)binary & 0x000000ff;
		if (0 != (hex & 0xfffffff0)) {
			sb.append(Integer.toHexString(hex));
		} else {
			sb.append("0" + Integer.toHexString(hex));
		}
		return sb.toString();
	}
	
	public static String getHash(String data){
		return getHash(data, "SHA-256");
	} 
	
	public static String getHash(String data, String algorithm) {// create a digest from character string
		MessageDigest md = null;
		try{
			md = MessageDigest.getInstance(algorithm);
		}catch(NoSuchAlgorithmException e){
			log.error("", e);
			return null;
		}
		
		byte[] dat = data.getBytes();
		md.update(dat);// calculate a digest from a dat arrangement.
		byte[] digest = md.digest();
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			int d = digest[i];
			if (d < 0) {// revise it because 128-255 become minus number value with byte type.
				d += 256;
			}
			if (d < 16) {// Because it become one column by a hex digit, if it is 0-15, we add "0" to a head to become two columns.
				sb.append("0");
			}
			sb.append(Integer.toString(d, 16));// display 1 byte of the digest value with hexadecimal two columns
		}
		
		return sb.toString();
	}
}