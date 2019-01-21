//package zsh.jl.zshui;
package ret.sec.oxygenauto.daemon;

import android.os.RemoteException;
import android.util.Log;
import android.app.UiAutomation;
import android.app.UiAutomationConnection;
import android.os.HandlerThread;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

//import android.app.ActivityManagerNative;

//import android.app.SystemServiceRegistry;


public class HttpDaemon {
    //因为线程的原因,把初始化HandlerThread的代码放到主线程中
    private static final String HANDLER_THREAD_NAME = "UiAutomatorHandlerThread";
    private final HandlerThread mHandlerThread = new HandlerThread(HANDLER_THREAD_NAME);

    //当有一个新任务来时，加下这个TASK INDEX，然后用这个INDEX去请求获取新任务所需要的参数
    private static int task_index = 0;

    //下面6个有新任务来时初始化
    private static Task task;

    private int thumbup;
    private int percent;
    private int page;
    private static int heart_beat_count = 0;
    private MockWebServer server = new MockWebServer();

    public HttpDaemon(int thumbup, int percent, int page, int port) {
        this.thumbup = thumbup;
        this.percent = percent;
        this.page = page;
        this.startHttp(port);
    }

    private void startHttp(int port) {
        System.out.println("Started http server on port:" + port);
        if (!mHandlerThread.isAlive()) {
            mHandlerThread.start();
        }


//        mUiAutomation.connect();
        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/invoke")) {
                    String json_string = request.getBody().readUtf8();
                    Log.d("Garri", "receive:" + json_string);
                    MockResponse response = new MockResponse();
                    try {
                        procedureCmd(response, json_string);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject cmd = new JSONObject();
                        try {
                            cmd.put("code", 1);
                            cmd.put("message", e.toString());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        response(response, cmd);
                    }

                    return response;
                }
                return new MockResponse().setResponseCode(404).setBody("{\"code\":404,\"message\":\"use /invoke to access\"}");
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
//            System.out.println("stopping...");
            server.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean preFilter(MockResponse remotePeer, JSONObject cmd) throws RemoteException, JSONException {
        String action = cmd.getString("action");
        switch (action) {
            case "exit":
//                output(remotePeer, "exit ok", "0");
                this.stop();
                return true;
            case "new":
                this.newTask(remotePeer);
                return true;
            case "heartBeat":
                heart_beat_count++;
                output(remotePeer, heart_beat_count + "", "0");
                return true;
            default:
                return false;
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
    private void procedureCmd(MockResponse remotePeer, String cmd) throws RemoteException {
        try {
            Log.d("Garri", "Recv:" + cmd);
            JSONObject j_cmd = new JSONObject(cmd);
            if (this.preFilter(remotePeer, j_cmd)) {
                return;
            }
            CmdHandle handle = new CmdHandle(task, mHandlerThread);
            handle.handle(remotePeer, j_cmd);
        } catch (JSONException e) {
            e.printStackTrace();
            this.error(remotePeer, e.getMessage());
        }
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

