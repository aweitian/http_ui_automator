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
    private static int task_index = 0;

    //下面6个有新任务来时初始化
    private static Task task;

    private int thumbup;
    private int percent;
    private int page;
    private static int heart_beat_count = 0;
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

    private void newTask(MockResponse remotePeer) throws JSONException {
        String sn = CmdHandle.run("getprop ro.serialno");
        String url = "https://xcx.sh9l.com/oxygen_auto_verify/getTaskInfo.php?sn=" + sn + "&i=" + task_index;
        String r = util.request_get(url);
        JSONObject j = new JSONObject(r);
        if (j.getInt("code") != 0) {
            task = null;
            error(remotePeer, j.getString("message"));
        } else {
            JSONObject data = j.getJSONObject("data");
            task = new Task();
            task.current_step = UiViewIdConst.NEXT_STEP_VPN_START;
            task.vpnServer = data.getString("vpn_host");
            task.vpnUser = data.getString("vpn_user");
            task.vpnPass = data.getString("vpn_pass");
            task.oxygenUser = data.getString("oxygen_user");
            task.oxygenPass = data.getString("oxygen_pass");
            task.app_start_time = data.getInt("oxygen_start_time");
            task.vpn_connect_time = data.getInt("vpn_connect_time");

            output(remotePeer, "ok", "0");
        }
    }

    /**
     * action
     * data
     *
     * @param remotePeer
     * @param cmd
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
                case "/new":
                    this.newTask(response);
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

