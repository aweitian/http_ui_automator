package ret.sec.oxygenauto.daemon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.util.Log;

import okhttp3.mockwebserver.MockResponse;


public class CmdHandle {
    private MockResponse mockResponse;
    private JSONObject cmd;
    private static String lastErr;

    public void handle(MockResponse remotePeer, JSONObject c) throws JSONException {
        this.mockResponse = remotePeer;
        this.cmd = c;
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

    public void test() throws JSONException {
        response("test", 0);
    }

    private void ok(String msg) throws JSONException {
        response(msg, 0);
    }

    public void list() throws JSONException {
        Method[] methods = this.getClass().getDeclaredMethods();
        JSONArray ms = new JSONArray();
        for (int i = 0; i < methods.length; i++) {
            //ms[i] = methods[i].getName();
            int m = methods[i].getModifiers();

            if (Modifier.isPublic(m) && methods[i].getParameterTypes().length == 0) {
                ms.put(methods[i].getName());
            }

        }
        response(ms, 1);
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
            Method method = obj.getClass().getDeclaredMethod(methodName, c);
            try {
                //4.通过方法所在的类，和具体的参数值，调用相应的方法
                int m = method.getModifiers();
                if (Modifier.isPublic(m)) {
                    method.invoke(obj, args);//调用o对象的方法
                    return 0;
                }
                return 2;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                lastErr = ex.getMessage();
                Logger.getLogger("Garri").log(Level.SEVERE, null, ex);
                return 1;
            }
        } catch (NoSuchMethodException ex) {
            Logger.getLogger("Garri").log(Level.SEVERE, null, ex);
            return 2;
        }
    }
}

