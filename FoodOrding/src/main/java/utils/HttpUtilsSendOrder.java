package utils;

import android.util.Log;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtilsSendOrder {
	private String path;
	private static InputStream inputStream;
	private static String jsonString;

	public HttpUtilsSendOrder(){

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
					conn.setRequestProperty("Content-type", "text/html");
					conn.setRequestProperty("Accept-Charset", "utf-8");
					conn.setRequestProperty("contentType", "utf-8");
					int code = conn.getResponseCode();
					if (code == 200) {
						Log.e("nihao","访问正常");
						inputStream = conn.getInputStream();
						callback.success(changeInputStream(inputStream)); 
						inputStream.close();
					}else{
						Log.e("nihao", conn.getResponseCode()+conn.getResponseMessage());
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("nihao", "访问失败");
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
	
