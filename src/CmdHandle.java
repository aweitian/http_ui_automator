package ret.sec.oxygenauto.daemon;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.UiAutomation;
import android.app.UiAutomationConnection;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;

import okhttp3.mockwebserver.MockResponse;


public class CmdHandle {
    private static String suffix = "Api";
    private MockResponse mockResponse;
    private JSONObject cmd;
    private static String lastErr;
    private String ime_lists;

    public void handle(MockResponse remotePeer, JSONObject c) throws JSONException {
        this.mockResponse = remotePeer;
        this.cmd = c;
//        try {
//            topActivityApi();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
        int ret = invoke(cmd.getString("action"), this, null);
        switch (ret) {
            case 0:
                return;
            case 1:
                error(lastErr);
                break;
            case 2:
                unknownCmd();
                break;
        }
    }


    //### API ########################################################################


    public void nextApi() {
//        Intent vpnIntent = new Intent();
//        vpnIntent.setAction("android.net.vpn.SETTINGS");
//        mActivity.startActivity(vpnIntent);
    }


    public void topActivityApi() throws JSONException, RemoteException {
//        IActivityManager mAm = ActivityManager.getService();
//        IBinder b = ServiceManager.getService("activity");
//        IActivityManager in = IActivityManager.Stub.asInterface(b);
        IActivityManager mAm = ActivityManagerNative.getDefault();
        final String HANDLER_THREAD_NAME = "UiAutomatorHandlerThread";
        HandlerThread mHandlerThread = new HandlerThread(HANDLER_THREAD_NAME);
        UiAutomation mUiAutomation;
        if (!mHandlerThread.isAlive()) {
//            throw new IllegalStateException("Already connected!");
            mHandlerThread.start();
        }

        mUiAutomation = new UiAutomation(mHandlerThread.getLooper(),
                new UiAutomationConnection());
        mUiAutomation.connect();

        ComponentName componentName;
        if (Build.VERSION.SDK_INT > 19) {
            componentName = Objects.requireNonNull(mAm).getTasks(1, 0).get(0).topActivity;
            ok(componentName.getShortClassName());
        } else {
            //java.util.List<android.app.ActivityManager.RunningTaskInfo> = in
            Log.e("Garri", "use reflect invoke ActivityManager.getTasks");
        }
        mUiAutomation.disconnect();
    }

    public void topPackageApi() throws JSONException, RemoteException {
//        IActivityManager mAm = ActivityManager.getService();
//        IBinder b = ServiceManager.getService("activity");
//        IActivityManager in = IActivityManager.Stub.asInterface(b);
        IActivityManager mAm = ActivityManagerNative.getDefault();
        final String HANDLER_THREAD_NAME = "UiAutomatorHandlerThread";
        HandlerThread mHandlerThread = new HandlerThread(HANDLER_THREAD_NAME);
        UiAutomation mUiAutomation;
        if (!mHandlerThread.isAlive()) {
//            throw new IllegalStateException("Already connected!");
            mHandlerThread.start();
        }

        mUiAutomation = new UiAutomation(mHandlerThread.getLooper(),
                new UiAutomationConnection());
        mUiAutomation.connect();

        ComponentName componentName;
        if (Build.VERSION.SDK_INT > 19) {
            componentName = Objects.requireNonNull(mAm).getTasks(1, 0).get(0).topActivity;
            ok(componentName.getPackageName());
        } else {
            //java.util.List<android.app.ActivityManager.RunningTaskInfo> = in
            Log.e("Garri", "use reflect invoke ActivityManager.getTasks");
        }
        mUiAutomation.disconnect();
    }


    public void testApi() throws JSONException {
        response(util.request_get("https://xcx.sh9l.com/oxygen_auto_verify/getTaskInfo.php?sn=29ca221a&i=2"), 0);
    }


    public void listApi() throws JSONException {
        Method[] methods = this.getClass().getDeclaredMethods();
        JSONArray ms = new JSONArray();
        for (Method method : methods) {
            //ms[i] = methods[i].getName();
            int m = method.getModifiers();

            if (Modifier.isPublic(m) && method.getParameterTypes().length == 0 && method.getName().endsWith(CmdHandle.suffix)) {
                ms.put(method.getName().replaceAll("Api$", ""));
            }
        }
        response(ms, 1);
    }

    public void disableInputApi() throws JSONException {
        ok("disabled " + disableInput() + " inputs");
    }

    public void enableInputApi() throws JSONException {
        ok("enabled " + enableInput() + " inputs");
    }

    //### API ########################################################################

    private int disableInput() throws JSONException {
        String ori_inp = run("ime list -s");
        int i = 0;
        String ois[] = ori_inp.split("\n");
        for (String input : ois) {
            if (!input.isEmpty()) {
                run("ime disable " + input);
                i++;
            }
        }
        util.filePutContents("/data/data/ret_sec_oxygenauto_daemon_input.txt", ori_inp);
        return i;
    }

    private int enableInput() throws JSONException {
        String ori_inp = util.fileGetContents("/data/data/ret_sec_oxygenauto_daemon_input.txt");
        if (ori_inp == null) {
            return 0;
        }
        int i = 0;
        String ois[] = ori_inp.split("\n");
        for (String input : ois) {
            if (!input.isEmpty()) {
                run("ime enable " + input);
                i++;
            }
        }
        return i;
    }

    private void startOxygen() {
        startActivity("com.youxiang.soyoungapp/com.youxiang.soyoungapp.ui.MainActivity");
    }

    private void startVpn() {
        startActivity("com.android.settings/com.android.settings.MainSettings");
    }

    private void startActivity(String name) {
        run("am start " + name);
    }

    private void ok(String msg) throws JSONException {
        response(msg, 0);
    }

    private void response(Object msg, int code) throws JSONException {
        JSONObject cmd = new JSONObject();
        cmd.put("code", code);
        cmd.put("message", msg);
        mockResponse.setBody(cmd.toString());
    }

    private void error(String msg) throws JSONException {
        response(msg, 1);
    }

    private void unknownCmd() throws JSONException {
        error("unknown cmd:" + cmd.getString("action"));
    }

    /**
     * @param methodName 方法名称
     * @param obj        调用此方法的对象
     * @param args       调用的这个方法的参数参数列表
     */
    private static int invoke(String methodName, Object obj, Object[] args) {
        Class c[] = null;

        //1.参数存在
        if (args != null) {
            int len = args.length;
            c = new Class[len];

            //2.根据参数得到相应的 Class的类对象
            for (int i = 0; i < len; ++i) {
                c[i] = args[i].getClass();
            }
        }
        try {
            //3.根据方法名，参数Class类对象数组，得到Method
            Method method = obj.getClass().getDeclaredMethod(methodName + CmdHandle.suffix, c);
            try {
                //4.通过方法所在的类，和具体的参数值，调用相应的方法
                int m = method.getModifiers();

                if (Modifier.isPublic(m) && method.getParameterTypes().length == 0) {
                    method.invoke(obj, args);//调用o对象的方法
                    return 0;
                }
                return 2;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                lastErr = ex.getMessage();
                Log.e("Garri", lastErr);
                ex.printStackTrace();
                return 1;
            }
        } catch (NoSuchMethodException ex) {
            Log.e("Garri", ex.getMessage());
            ex.printStackTrace();
            return 2;
        }
    }

    static String run(String cmd) {
        Process process;
        try {
            process = Runtime.getRuntime().exec("sh");
            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(process.getInputStream());
            dataOutputStream.writeBytes(cmd + "\n");
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            InputStreamReader inputStreamReader = new InputStreamReader(
                    dataInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            final StringBuilder out = new StringBuilder();
            final int bufferSize = 1024;
            final char[] buffer = new char[bufferSize];
            for (; ; ) {
                int rsz = inputStreamReader.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }

            bufferedReader.close();
            inputStreamReader.close();
            process.waitFor();
            return out.toString();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }
}

