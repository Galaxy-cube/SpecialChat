package cc.specialchat.specialchatserver;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Some often used tools packed in methods below.
 * @author  Zhong Wenliang
 * @mail    CuberWenliang@163.com
 * @date    20.03
 */

public class MyTools{
	
	static String createANewTokenKey(){
		StringBuffer stringBuffer=new StringBuffer();
		String[] charSets=new String[]{"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
		stringBuffer.append(charSets[getRandomNum(15,1)]);
		for(int i=1;i<=15;i++){
			stringBuffer.append(charSets[getRandomNum(16,1)-1]);
		}
		return stringBuffer.toString();
	}
	
	/**
	 * Get a random number between max and min
	 * @param max max number
	 * @param min min number SHOULD NOT BE 0!!!
	 * @return a integer
	 */
	static int getRandomNum(int max,int min){
		return (int)(1+Math.random()*(max-min+1));
	}
	
	/**
	 * Get current timestamp
	 * @return current timestamp (integer) , 10 bits
	 * **/
	static int getCurrentTime(){
		return (int)(System.currentTimeMillis()/1000);
	}
	
	/**
	 * Use md5 encrypt input String. Found By the Internet
	 * @param string to be encrypt
	 * @return String
	 * **/
	public static String md5(String string){
		if(string==null || string.isEmpty()){
			return null;
		}
		try{
			byte[] bytes = MessageDigest.getInstance("MD5").digest(string.getBytes());
			StringBuilder result =new StringBuilder();
			for (byte b : bytes) {
				String temp = Integer.toHexString(b & 0xff);
				if (temp.length() == 1) {
					temp = "0" + temp;
				}
				result.append(temp);
			}
			return result.toString();
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}
		return null;
	}
	
}