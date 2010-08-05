/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

package org.infoscoop.util;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.service.PropertiesService;

public class RSAKeyManager {
	private static final BigInteger MODULUS = new BigInteger("dc93a1b23949845fbf3643d058e8450b25b2d829c4f48eebd0bee7a303e73be579b590313d8d904c323a7982e862bca72774462073de1f9a46b48bcd5ba3252b",16 );
	private static final BigInteger EXPORNENT = new BigInteger("7614e25f12a3f4a57d2b83aa29ae4e27e58a9485e7d6341cb1a74141d779166a87aef6443e0309ebd5a83b3fce48f6e4a917e714e5c9174743dd7ed6cc19d3e1",16 );

	private static Log log = LogFactory.getLog(RSAKeyManager.class);
	private static String rsaAlgorithm = "RSA";
	private static KeyFactory keyFactory;
	static{
		try {

			keyFactory = KeyFactory.getInstance("RSA");
			if(log.isInfoEnabled())
				log.info("Use RSA Provider:" + keyFactory.getProvider().getName() );
		} catch (NoSuchAlgorithmException e) {
			log.error("",e);
		}
	}

	private static RSAKeyManager manager;

	private RSAPublicKey publicKey;
	private RSAPrivateKey privateKey;


	public RSAKeyManager( BigInteger modulus,BigInteger exponent ) throws NoSuchAlgorithmException, NoSuchProviderException,InvalidKeySpecException {

		if(log.isInfoEnabled())
			log.info("### Using RSAProvider is " + keyFactory.getProvider().getClass());

		RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec( modulus,exponent );
		privateKey = ( RSAPrivateKey )keyFactory.generatePrivate( privateKeySpec );

		RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec( modulus,RSAKeyGenParameterSpec.F4 );
		publicKey = ( RSAPublicKey )keyFactory.generatePublic( publicKeySpec );

	}

	public static RSAKeyManager getInstance(){
		if(manager == null){
			try {
				PropertiesService propServ = ( PropertiesService )SpringUtil.getBean("PropertiesService");

				manager = new RSAKeyManager(
						new BigInteger( propServ.getProperty("keyManagerModulus"),16 ),
						new BigInteger( propServ.getProperty("keyManagerExponent"),16 ) );
			} catch(Exception e) {
				log.error("Get invalid key from properties table.", e);
				try {
					manager = new RSAKeyManager(MODULUS, EXPORNENT);
				} catch (Exception e1) {
					log.error("Failed initalization RSAKeyManager used default key", e);
				}
			}
		}
		return manager;
	}

	public String getPublicExponent(){
		return publicKey.getPublicExponent().toString( 16 );
	}

	public String getModulus(){
		return new String( publicKey.getModulus().toString( 16 ));
	}

	public String decrypt(String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalStateException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException{
		if("".equals(text))return text;

		Cipher cipher = Cipher.getInstance(rsaAlgorithm);
		cipher.init(Cipher.ENCRYPT_MODE,publicKey );

		byte[] crypted = new BigInteger(text,16).toByteArray();

		cipher.init( Cipher.DECRYPT_MODE, privateKey );

		int offset = crypted[0] == 0 ? 1 : 0;
		int length = crypted.length - offset;
		byte[] decrypted = cipher.doFinal( crypted, crypted[0] == 0 ? 1 : 0, length);
		return new String( new BigInteger(decrypted).toByteArray() );
	}

	public String encrypt(String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalStateException, IllegalBlockSizeException, BadPaddingException{

		Cipher cipher = Cipher.getInstance(rsaAlgorithm);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encrypted = cipher.doFinal(text.getBytes());

		return new BigInteger(1,encrypted).toString(16);

	}

	public static void main( String[] args ) throws Exception {
		System.out.println(System.getProperty("java.version"));
		System.out.println("Modulus:" + RSAKeyManager.getInstance().getModulus());
		System.out.println("Exponent:" + RSAKeyManager.getInstance().getPublicExponent());
		System.out.println("Private:" + RSAKeyManager.getInstance().getPrivateKey());

		String encriptStr = RSAKeyManager.getInstance().encrypt("6600103");
		//String encriptStr = "4a015923342985dafd443b17227a469991c7b00a067e6700237b92c2c7db6c2fa51f06829d21bf0445be07ce9605a5b0b821aa7d49bfa94813049ca867f2e2";
		//String encriptStr = "5c4b200f86d7ab7c2974fd6a4d94ba4dc4beb1160252beff8f8b6017c77d5fce4b05d29d4610208d50f077e6b95a6c5e226ed427e649d7d81c2fa293a088ede8";
		//String encriptStr = "276554dc5a50b887cb4ea3b64893eca267574b5188f9cf7ead6910051921b910aee0eab9d70c391099d3ee923eb28287b5a9f3ffff647626b25dbb79e6ddacd0";
		System.out.println("Encrypt String:" + encriptStr);
		String decriptStr = RSAKeyManager.getInstance().decrypt(encriptStr);

		System.out.println("Decrypted: "+decriptStr);

		System.out.println(new BigInteger(-1, "11".getBytes()));
	}

	private String getPrivateKey() {
		return privateKey.getPrivateExponent().toString(16);
	}
}
