package ret.sec.oxygenauto.daemon;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

class util {
    static String fileGetContents(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            int i = in.read(filecontent);
            in.close();
            if (i == 0)
                return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    static void filePutContents(String fileName, String txt) {
        File file = new File(fileName);
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(txt.getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String request_get(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            String json_str = response.body().string();
            //System.out.println(json_str);
            //RunOnUiThread();
            Log.d("Garri", json_str);
            return json_str;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Garri", e.getMessage());
            return null;
        }
    }
}
