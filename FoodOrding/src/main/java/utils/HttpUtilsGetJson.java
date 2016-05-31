package utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpUtilsGetJson {
	private String path;
	private static InputStream inputStream;
	private static String jsonString;

	public HttpUtilsGetJson(){

	}
	public interface CallBack{
		void success(String jsonString);
		void error(Exception e);
	}

	public  void getJSON(final String path, final CallBack callback) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					URL url = new URL(path);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("POST");
					conn.setConnectTimeout(5000);
					int code = conn.getResponseCode();
					if (code == 200) {
						inputStream = conn.getInputStream();
						callback.success(changeInputStream(inputStream)); 
						inputStream.close();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callback.error(e);
				}
			}
		}).start();
	
}
	private static String changeInputStream(InputStream inputStream2) {
		// TODO Auto-generated method stub
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int len = 0;
		byte[] data = new byte[1024];
		try {
			while ((len = inputStream2.read(data)) != -1) {
				byteArrayOutputStream.write(data, 0, len);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonString=new String(byteArrayOutputStream.toByteArray());
		return jsonString;
	}
}
	
