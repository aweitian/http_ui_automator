//package zsh.jl.zshui;
package ret.sec.oxygenauto.daemon;

import android.os.RemoteException;
import android.util.Log;
import android.app.UiAutomation;
import android.app.UiAutomationConnection;
import android.os.HandlerThread;
import android.view.accessibility.AccessibilityNodeInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

//import android.app.ActivityManagerNative;

//import android.app.SystemServiceRegistry;


public class HttpDaemon {
    //因为线程的原因,把初始化HandlerThread的代码放到主线程中
    //当有一个新任务来时，加下这个TASK INDEX，然后用这个INDEX去请求获取新任务所需要的参数
    public static int task_index = 0;

    //下面6个有新任务来时初始化
    public static Task task;

    public static int thumbup;
    public static int percent;
    public static int page;
    public static int heart_beat_count = 0;
    public static MockWebServer server = new MockWebServer();

    public HttpDaemon(int thumbup, int percent, int page, int port) {
        this.thumbup = thumbup;
        this.percent = percent;
        this.page = page;
        this.startHttp(port);
    }

    private void startHttp(int port) {
        System.out.println("Started http server on port:" + port);

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                try {
                    return procedureCmd(request.getPath());
                } catch (Exception exception) {
                    return new MockResponse().setResponseCode(200).setBody("{\"code\":500,\"message\":\"" + exception.getMessage() + "\"}");
                }
            }
        };
        server.setDispatcher(dispatcher);
        try {
            InetAddress wildCard = new InetSocketAddress(0).getAddress();
            server.start(wildCard, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            server.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * action
     * data
     *
     * @param url
     */
    private MockResponse procedureCmd(String url) throws RemoteException {
        MockResponse response = new MockResponse();
        try {
            Log.d("Garri", "Recv:" + url);
            switch (url) {
                case "/heartBeat":
                    heart_beat_count++;
                    output(response, heart_beat_count + "", "0");
                    break;
                case "/exit":
                    this.stop();
                    break;

                default:
                    CmdHandle handle = new CmdHandle(task);
                    handle.handle(response, url);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            this.error(response, e.getMessage());
        }
        return response;
    }

    private void response(MockResponse r, JSONObject cmd) {
        Log.d("Garri", cmd.toString());
        r.setBody(cmd.toString());
    }

    private void error(MockResponse remotePeer, String text) {
        output(remotePeer, text, "1");
    }

    private void output(MockResponse remotePeer, String text, String code) {
        JSONObject cmd = new JSONObject();
        try {
            cmd.put("code", code);
            cmd.put("message", text);
            response(remotePeer, cmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

