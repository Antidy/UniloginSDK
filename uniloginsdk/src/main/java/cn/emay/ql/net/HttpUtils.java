package cn.emay.ql.net;


import android.util.Log;

import cn.emay.ql.utils.GzipUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {
    private static final MediaType MEDIA_TYPE_PLAINTEXT = MediaType
            .parse("application/json;charset=utf-8");
    public static void sendRequest(final String url,final String appid,final String aesKey,final byte[] body, final HttpCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestBody requestBody = RequestBody.create(MEDIA_TYPE_PLAINTEXT, body);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().addHeader("appId", appid).addHeader("gzip","on")
                            .url(url).post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    String code = response.header("result");
                    if (response.isSuccessful()){
                        if (code.equals("SUCCESS")){
                            byte[] result = response.body().bytes();
                            result = AES.decrypt(result, aesKey,"UTF-8");
                            result = GzipUtils.decompress(result);
                            String res = new String(result,"UTF-8");
                            callback.onSuccess(res);
                        }else{
                            callback.onFailed(code + "");
                        }
                    }else {
                        callback.onFailed(response.code() + "");
                    }

                }catch (Exception e){
                    callback.onFailed(e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
