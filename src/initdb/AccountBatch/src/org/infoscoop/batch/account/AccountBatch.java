package org.infoscoop.batch.account;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class AccountBatch {
	public static void main( String[] argv ) throws Exception {
		String fileName = argv[0];
		File file = new File( fileName );
		if( !file.exists()) {
			System.out.println("File Not Found: "+fileName );
			return;
		}
		
		CSVReader reader = new CSVReader( new InputStreamReader( new FileInputStream( file ),"UTF-8" ));
		CSVWriter writer = new CSVWriter( new OutputStreamWriter( new FileOutputStream(
				new File( file.getAbsolutePath()+".opt")),"UTF-8"));

		int ln = 0;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA");
			
			String[] fields;
			while( ( fields = reader.readNext() ) != null ) {
				ln++;
				
				String uid = fields[0];
				String name = fields[1];
				String pass = fields[2];
				
				String hash = new String( Base64.encodeBase64( digest.digest( pass.getBytes("iso-8859-1"))));
				writer.writeNext( new String[]{ uid,name,hash });
			}
		} catch( Exception ex ) {
			System.out.println("#"+ln+",,,"+ex.getMessage());
			ex.printStackTrace();
		} finally {
			reader.close();
			writer.close();
		}
	}
}