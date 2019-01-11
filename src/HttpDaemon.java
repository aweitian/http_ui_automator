//package zsh.jl.zshui;
package ret.sec.oxygenauto;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.UiAutomation;
import android.app.UiAutomationConnection;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.IClipboard;
import android.hardware.input.InputManager;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import android.os.Build;

import static android.hardware.input.InputManager.INJECT_INPUT_EVENT_MODE_ASYNC;

//import android.app.SystemServiceRegistry;


public class HttpDaemon {
    private MockWebServer server = new MockWebServer();

    private HttpDaemon(int port) {
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
            server.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean preFilter(MockResponse remotePeer, JSONObject cmd) throws RemoteException {
        String action = cmd.getString("action");
        switch (action) {
            case "exit":
                this.stop();
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

    private void unknowCmd(MockResponse remotePeer, String text) {
        JSONObject cmd = new JSONObject();
        try {
            cmd.put("code", 1);
            cmd.put("message", "unknown cmd:" + text);
            response(remotePeer, cmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void error(MockResponse remotePeer, String text) {
        JSONObject cmd = new JSONObject();
        try {
            cmd.put("code", 1);
            cmd.put("message", text);
            response(remotePeer, cmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
