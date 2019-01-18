//package zsh.jl.zshui;
package ret.sec.oxygenauto.daemon;

import android.os.RemoteException;
import android.util.Log;

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
    private static String next_step = UiViewIdConst.NEXT_STEP_VPN_START;
    private static int task_index = 0;

    //下面4个有新任务来时初始化
    private static String search_keyword;
    private static String vpn_host;
    private static String vpn_user;
    private static String vpn_pass;
    private static String oxygen_user;
    private static String oxygen_pass;


    private static int heart_beat_count = 0;
    private MockWebServer server = new MockWebServer();

    public HttpDaemon(int port) {
        this.startHttp(port);
    }

    private void startHttp(int port) {
        System.out.println("Started http server on port:" + port);

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
            case "heartBeat":
                heart_beat_count++;
                output(remotePeer, heart_beat_count + "", "0");
                return true;
            default:
                return false;
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
            CmdHandle handle = new CmdHandle();
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

