package utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtilsAddToLove {
    private String path;
    private static InputStream inputStream;

    public HttpUtilsAddToLove() {

    }

    public interface CallBack {
        void success();

        void error(Exception e);
    }

    public void AddtoLove(final String path, final CallBack callback) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        callback.success();
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    callback.error(e);
                }
            }
        }).start();

    }
}

