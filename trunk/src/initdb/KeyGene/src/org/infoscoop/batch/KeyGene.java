package org.infoscoop.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.apache.commons.codec.binary.Base64;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class KeyGene {
	public static void main( String[] argv ) throws Exception {
		String fileName = argv[0];
		File file = new File( fileName );
		if( !file.exists()) {
			System.out.println("File Not Found: "+fileName );
			return;
		}

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize( 512, new SecureRandom());
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		RSAPrivateKey privateKey = ( RSAPrivateKey )keyPair.getPrivate();
		
		StringBuffer buf = new StringBuffer();
		BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( file ),"UTF-8" ));
		Writer writer = new OutputStreamWriter( new FileOutputStream(
				new File( file.getAbsolutePath()+".opt") ),"UTF-8");
		try {
			String line = "";
			while (( line = reader.readLine()) != null )
				buf.append( line ).append("\n");
			
			String str = buf.toString()
				.replaceAll("@keyManagerModulus@",privateKey.getModulus().toString(16))
				.replaceAll("@keyManagerExponent@",privateKey.getPrivateExponent().toString(16));
			
			writer.write( str );
		} catch( Exception ex ) {
			System.out.println( ex.getMessage());
			ex.printStackTrace();
		} finally {
			reader.close();
			writer.close();
		}
	}
}