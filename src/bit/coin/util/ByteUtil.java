package bit.coin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;


public class ByteUtil {

	public static byte[] toByteArray(File file, long start, long count) throws Exception {
	      long length = file.length();
	      
	      if (start >= length) return new byte[0];
	      
	      count = Math.min(count, length - start);
	      
	      InputStream in = new FileInputStream(file);
	     
	      return toByteArray(in, start, count);
	}
	
	public static byte[] toByteArray(InputStream in, long start, long count) throws Exception {

		in.reset();
		in.skip(start);
		
		byte[] array = new byte[(int) count];

		long offset = 0;

		while (offset < count) {
			int tmp = in.read(array, (int) offset, (int) count);
			offset += tmp;
		}

		in.close();

		return array;
	}
	
	public static byte[] readByte(InputStream in, long count) throws Exception {
		return readByte(in, count, false);
	}
	
	public static byte[] readByte(InputStream in, long count, boolean reverse) throws Exception {
		
		byte[] array = new byte[(int) count];

		long offset = 0;

		while (offset < count) {
			int tmp = in.read(array, (int) offset, (int) count);
			offset += tmp;
		}

		if (reverse) {
			reverseArray(array);
		}

		return array;
	}
	
	public static String readHex(InputStream in, long count) throws Exception {
		return bytesToHex(readByte(in, count, true));
	}
	
	public static int readInt(InputStream in, long count) throws Exception {
		return (new BigInteger(1, readByte(in, count, true))).intValue();
	}
	
	public static long readLong(InputStream in, long count) throws Exception {
		return (new BigInteger(1, readByte(in, count, true))).longValue();
	}
	
	public static long readVarInt(InputStream in) throws Exception {
		long size = readInt(in, 1);

		if (size < 0xfd) {
			return size;
		}
		else if (size == 0xfd) {
			return readLong(in, 2);
		} 
		else if (size == 0xfe) {
			return readLong(in, 4);
		} 
		else if (size == 0xff) {
			return readLong(in, 8);
		}
		
		return -1;
	}
	
	public static String bytesToHex(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return "NULL";
		}
		
	    char[] hexChars = new char[bytes.length * 2];
	    
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    
	    return new String(hexChars);
	}
	
	private static void reverseArray(byte[] array) {
		if (array == null) {
			return;
		}
		int i = 0;
		int j = array.length - 1;
		byte tmp;
		while (j > i) {
			tmp = array[j];
			array[j] = array[i];
			array[i] = tmp;
			j--;
			i++;
		}
	}

	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
}
