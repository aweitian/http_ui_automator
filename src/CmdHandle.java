package ret.sec.oxygenauto.daemon;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.UiAutomation;
import android.app.UiAutomationConnection;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.IClipboard;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.os.Build;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

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
    private UiAutomation mUiAutomation;
    private HandlerThread mHandlerThread;
    private Task task;

    public CmdHandle(Task task, HandlerThread mHandlerThread) {
        this.mHandlerThread = mHandlerThread;
        this.task = task;
    }

    public void handle(MockResponse remotePeer, JSONObject c) throws JSONException, RemoteException {
        this.mockResponse = remotePeer;
        this.cmd = c;

//        try {
//            topActivityApi();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
        //int ret = invoke(cmd.getString("action"), this, null);
        switch (cmd.getString("action")) {
            case "test":
                testApi();
                break;
            case "next":
                if (!isValidTask())
                    break;
                nextApi();
                break;
            case "back":
                if (!isValidTask())
                    break;
                backApi();
                break;
            case "done":
                if (!isValidTask())
                    break;
                doneApi();
                break;
            case "stop":
                if (!isValidTask())
                    break;
                stopApi();
                break;
            default:
                unknownCmd();
                break;
        }
    }

    private boolean isValidTask() throws JSONException {
        if (task == null) {
            error("new a task first");
            return false;
        }
        return true;
    }


    //### API ########################################################################

    public void nextApi() throws RemoteException, JSONException {
        final String HANDLER_THREAD_NAME = "UiAutomatorHandlerThread";
        HandlerThread mHandlerThread = new HandlerThread(HANDLER_THREAD_NAME);
        UiAutomation mUiAutomation;
        if (!mHandlerThread.isAlive()) {
            mHandlerThread.start();
        }

        mUiAutomation = new UiAutomation(mHandlerThread.getLooper(),
                new UiAutomationConnection());
        mUiAutomation.connect();
        switch (task.current_step) {
            case UiViewIdConst.NEXT_STEP_VPN_START:
                next_step_vpn_start();
                task.current_step = UiViewIdConst.NEXT_STEP_VPN_CHECK;
                break;
            case UiViewIdConst.NEXT_STEP_VPN_CHECK:
                next_step_vpn_check();
                break;
            case UiViewIdConst.NEXT_STEP_OXYGEN_START:
                next_step_oxygen_start(mUiAutomation);
                break;
            case UiViewIdConst.NEXT_STEP_OXYGEN_STOP:
                next_step_oxygen_stop(mUiAutomation);
                break;
            case UiViewIdConst.NEXT_STEP_VPN_STOP:
                next_step_vpn_stop();
                break;
        }
        mUiAutomation.disconnect();
        mUiAutomation.destroy();
//        //主页面
//        if (componentName.getShortClassName().equals(UiViewIdConst.ACTIVITY_NAME_MAIN)) {
//            node = getSearchBtn();
//            if (node != null) {
//                performClick(node);
//                node.recycle();
//                responseWxResult(remotePeer, 0, "next entry search", UiViewIdConst.RESPONSE_RESULT_WX_NEXT);
//                return;
//            }
//            node = getFirstMainBtn();
//            if (node != null) {
//                performClick(node);
//                node.recycle();
//                responseWxResult(remotePeer, 0, "next entry first main page", UiViewIdConst.RESPONSE_RESULT_WX_NEXT);
//                return;
//            }
//            responseWxResult(remotePeer, 1, "在HOME首页找不到搜索按钮", UiViewIdConst.RESPONSE_RESULT_WX_STOP);
//            return;
//        }
//        //搜索页面
//        if (componentName.getShortClassName().equals(UiViewIdConst.ACTIVITY_NAME_SEARCH)) {
//            node = getNotFoundTextView();
//            if (node != null) {
//                node.recycle();
//                responseWxResult(remotePeer, 0, "not found,back to main page", UiViewIdConst.RESPONSE_RESULT_WX_BACK);
//                return;
//            }
//
//            node = getSearchResultBtn();
//            if (node != null) {
//                performClick(node);
//                node.recycle();
//                responseWxResult(remotePeer, 0, "next appear result", UiViewIdConst.RESPONSE_RESULT_WX_NEXT);
//                return;
//            }
//
//            node = getSearchInput();
//            if (node != null) {
//                //node.performAction(AccessibilityNodeInfo.A);
//                pasteText(node, phone_no);
//                node.recycle();
//                responseWxResult(remotePeer, 0, "next entry search", UiViewIdConst.RESPONSE_RESULT_WX_NEXT);
//                return;
//            }
//            responseWxResult(remotePeer, 1, "在SEARCH首页找不到搜索按钮", UiViewIdConst.RESPONSE_RESULT_WX_STOP);
//            return;
//        }
//
//        //添加联系人页面
//        if (componentName.getShortClassName().equals(UiViewIdConst.ACTIVITY_NAME_CONTACT)) {
//            node = getAddContactBtn();
//            if (node != null) {
//                performClick(node);
//                node.recycle();
//                responseWxResult(remotePeer, 0, "next entry search", UiViewIdConst.RESPONSE_RESULT_WX_NEXT);
//                return;
//            }
//            responseWxResult(remotePeer, 1, "在联系人页面找不到添加按钮", UiViewIdConst.RESPONSE_RESULT_WX_STOP);
//            return;
//        }
//
//        //发送验证页面
//        if (componentName.getShortClassName().equals(UiViewIdConst.ACTIVITY_NAME_VERIFY)) {
//            node = getPermissSendBtn();
//            if (node != null) {
//                performClick(node);
//                node.recycle();
//                responseWxResult(remotePeer, 0, "next entry search", UiViewIdConst.RESPONSE_RESULT_WX_DONE);
//                return;
//            }
//            responseWxResult(remotePeer, 1, "在发送验证页面找不到发送按钮", UiViewIdConst.RESPONSE_RESULT_WX_STOP);
//            return;
//        }
//        if (componentName.getPackageName().equals("com.tencent.mm")) {
//            responseWxResult(remotePeer, 1, "未知 ACTIVITY", UiViewIdConst.RESPONSE_RESULT_WX_STOP);
//        } else {
//            responseWxResult(remotePeer, 0, "wx is not start", UiViewIdConst.RESPONSE_RESULT_WX_START);
//        }
    }

    public void backApi() throws RemoteException, JSONException {
        final String HANDLER_THREAD_NAME = "UiAutomatorHandlerThread";
        HandlerThread mHandlerThread = new HandlerThread(HANDLER_THREAD_NAME);
        UiAutomation mUiAutomation;
        if (!mHandlerThread.isAlive()) {
            mHandlerThread.start();
        }

        mUiAutomation = new UiAutomation(mHandlerThread.getLooper(),
                new UiAutomationConnection());
        mUiAutomation.connect();
        switch (task.current_step) {
            case UiViewIdConst.NEXT_STEP_VPN_START:
                next_step_vpn_start();
                break;
            case UiViewIdConst.NEXT_STEP_OXYGEN_START:
                next_step_oxygen_start(mUiAutomation);
                break;
            case UiViewIdConst.NEXT_STEP_OXYGEN_STOP:
                next_step_oxygen_stop(mUiAutomation);
                break;
            case UiViewIdConst.NEXT_STEP_VPN_STOP:
                next_step_vpn_stop();
                break;
        }
        mUiAutomation.disconnect();
        mUiAutomation.destroy();
    }

    public void doneApi() throws RemoteException, JSONException {
        final String HANDLER_THREAD_NAME = "UiAutomatorHandlerThread";
        HandlerThread mHandlerThread = new HandlerThread(HANDLER_THREAD_NAME);
        UiAutomation mUiAutomation;
        if (!mHandlerThread.isAlive()) {
            mHandlerThread.start();
        }

        mUiAutomation = new UiAutomation(mHandlerThread.getLooper(),
                new UiAutomationConnection());
        mUiAutomation.connect();
        switch (task.current_step) {
            case UiViewIdConst.NEXT_STEP_VPN_START:
                next_step_vpn_start();
                break;
            case UiViewIdConst.NEXT_STEP_OXYGEN_START:
                next_step_oxygen_start(mUiAutomation);
                break;
            case UiViewIdConst.NEXT_STEP_OXYGEN_STOP:
                next_step_oxygen_stop(mUiAutomation);
                break;
            case UiViewIdConst.NEXT_STEP_VPN_STOP:
                next_step_vpn_stop();
                break;
        }
        mUiAutomation.disconnect();
        mUiAutomation.destroy();
    }


    public void stopApi() throws RemoteException, JSONException {

    }

//    public void topActivityApi() throws JSONException, RemoteException {
//        ok(getTopActivity());
//    }
//
//    public void topPackageApi() throws JSONException, RemoteException {
//        ok(getPackageName());
//    }


    public void testApi() throws JSONException, RemoteException {
        if (isVpnConnected()) {
            ok("connected.");
        } else {
            error("lost connection.");
        }
//        task = new Task();
//        task.vpnServer = "sh.upptp.com";
//        task.vpnUser = "bc022475";
//        task.vpnPass = "8888";
//        next_step_vpn_start();
        //response(util.request_get("https://xcx.sh9l.com/oxygen_auto_verify/getTaskInfo.php?sn=29ca221a&i=2"), 0);
    }


//    public void listApi() throws JSONException {
//        Method[] methods = this.getClass().getDeclaredMethods();
//        JSONArray ms = new JSONArray();
//        for (Method method : methods) {
//            //ms[i] = methods[i].getName();
//            int m = method.getModifiers();
//
//            if (Modifier.isPublic(m) && method.getParameterTypes().length == 0 && method.getName().endsWith(CmdHandle.suffix)) {
//                ms.put(method.getName().replaceAll("Api$", ""));
//            }
//        }
//        response(ms, 1);
//    }

//    public void disableInputApi() throws JSONException {
//        ok("disabled " + disableInput() + " inputs");
//    }
//
//    public void enableInputApi() throws JSONException {
//        ok("enabled " + enableInput() + " inputs");
//    }

    //### API ########################################################################

    private void next_step_vpn_start() throws RemoteException, JSONException {
        run("mtpd rmnet_data0 pptp " + task.vpnServer + " 1723 name " + task.vpnUser + " password " + task.vpnPass + " linkname vpn refuse-eap nodefaultroute idle 1800 mtu 1400 mru 1400 nomppe unit 0 &");
        next(task.current_step, task.app_start_time);
    }

    private void next_step_vpn_check() throws RemoteException, JSONException {
        if (isVpnConnected()) {
            //添加路由
            run("ip ro add default dev ppp0 table 0x3c");
            next(task.current_step, 1);
        } else {
            task.current_step = UiViewIdConst.NEXT_STOP;
            next(task.current_step, 1);
        }
    }

    private boolean isVpnConnected() throws RemoteException, JSONException {
        String r = run("ps | grep mtpd");
        return !r.isEmpty();
    }

    private void next_step_vpn_stop() {
        run("killall -15 mtpd");
        //run("killall -9 mtpd pppd");
    }

    private void next_step_oxygen_start(UiAutomation uiAutomation) {

    }

    private void next_step_oxygen_stop(UiAutomation uiAutomation) {

    }

    private String getPackageName() throws RemoteException {
        ComponentName componentName;
        if (Build.VERSION.SDK_INT > 19) {
            componentName = Objects.requireNonNull(getActivityManager()).getTasks(1, 0).get(0).topActivity;
            return componentName.getPackageName();
        } else {
            //java.util.List<android.app.ActivityManager.RunningTaskInfo> = in
            Log.e("Garri", "use reflect invoke ActivityManager.getTasks");
        }
        return "use reflect invoke ActivityManager.getTasks";
    }

    private String getTopActivity() throws RemoteException {
        ComponentName componentName;
        if (Build.VERSION.SDK_INT > 19) {
            componentName = Objects.requireNonNull(getActivityManager()).getTasks(1, 0).get(0).topActivity;
            return componentName.getClassName();
        } else {
            //java.util.List<android.app.ActivityManager.RunningTaskInfo> = in
            Log.e("Garri", "use reflect invoke ActivityManager.getTasks");
        }
        return "use reflect invoke ActivityManager.getTasks";
    }

    private IActivityManager getActivityManager() {
        return ActivityManagerNative.getDefault();
    }

    private void performClick(AccessibilityNodeInfo node) {
        performClick(node, false);
    }

    private void performClick(AccessibilityNodeInfo node, boolean input) {
        if (!input) {
            while (node != null && !node.isClickable()) {
                node = node.getParent();
            }
        }
        if (node != null) {
            if (input) {
                Rect rect = node.getBoundsInScreen();
                int left, top;
                left = rect.left + 2;
                top = rect.top + 2;
                run("input tap " + left + " " + top);
            } else {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }


    private void startOxygen() {
        startActivity("com.youxiang.soyoungapp/com.youxiang.soyoungapp.ui.MainActivity");
    }

//    private void startSettings() {
//        startActivity("com.android.settings/com.android.settings.MainSettings");
//    }

    private void startActivity(String name) {
        run("am start " + name);
    }

    private void next(String cur_step, int sleep) throws JSONException {
        JSONObject cmd = new JSONObject();
        cmd.put("code", 0);
        JSONObject data = new JSONObject();
        data.put("next", "next");
        data.put("delay", sleep);
        data.put("current", cur_step);
        cmd.put("message", "ok");
        mockResponse.setBody(cmd.toString());
    }

    private void back(int sleep) throws JSONException {
        JSONObject cmd = new JSONObject();
        cmd.put("code", 0);
        JSONObject data = new JSONObject();
        data.put("next", "back");
        data.put("delay", sleep);
        cmd.put("message", "ok");
        mockResponse.setBody(cmd.toString());
    }


    private void ok(String msg) throws JSONException {
        response(msg, 0);
    }

    private void response(Object msg, int code) throws JSONException {
        response(msg, code, null);
    }

    private void response(Object msg, int code, String data) throws JSONException {
        JSONObject cmd = new JSONObject();
        cmd.put("code", code);
        if (data != null)
            cmd.put("data", data);
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
                if (lastErr != null)
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

    private void pasteText(AccessibilityNodeInfo info, String text) throws RemoteException {
        IClipboard clipboardManager;
        IBinder b = ServiceManager.getService("clipboard");
        clipboardManager = IClipboard.Stub.asInterface(b);

        ClipData clip = ClipData.newPlainText("text", text);
        //clipboard.setPrimaryClip(clip);
        clipboardManager.setPrimaryClip(clip, text);
        //焦点（n是AccessibilityNodeInfo对象）
        info.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
        ////粘贴进入内容
        info.performAction(AccessibilityNodeInfo.ACTION_PASTE);
    }

    public UiAutomation getUiAutomation() {
        return new UiAutomation(mHandlerThread.getLooper(),
                new UiAutomationConnection());
    }
}

