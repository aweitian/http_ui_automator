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
import android.service.autofill.IFillCallback;
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
import java.util.Random;

import okhttp3.mockwebserver.MockResponse;


public class CmdHandle {
    private static String suffix = "Api";
    private MockResponse mockResponse;
    private String url;
    private static String lastErr;
    private UiAutomation mUiAutomation;
    private Task task;

    public CmdHandle(Task task) {
        this.task = task;
    }

    public void handle(MockResponse remotePeer, String url) throws RemoteException, JSONException {
        this.mockResponse = remotePeer;
        this.url = url;

        switch (url) {
            case "/next":
                if (!isValidTask())
                    break;
                nextApi();
                break;
            case "/new":
                this.newApi();
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


    public void nextApi() throws JSONException, RemoteException {
        mUiAutomation = Daemon.mUiAutomation;
        switch (task.current_step) {
            case UiViewIdConst.NEXT_STEP_VPN_START:
                next_step_vpn_start();
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

    //### API ########################################################################

    private void newApi() throws JSONException {
        String sn = CmdHandle.run("getprop ro.serialno");
        String url = UiViewIdConst.TASK_FEED_URL + "?sn=" + sn + "&i=" + HttpDaemon.task_index;
        String r = util.request_get(url);
        JSONObject j = new JSONObject(r);
        if (j.getInt("code") != 0) {
            task = null;
            error(j.getString("message"));
        } else {
            JSONObject data = j.getJSONObject("data");
            Task task = new Task();
            task.current_step = UiViewIdConst.NEXT_STEP_VPN_START;
            task.vpnServer = data.getString("vpn_host");
            task.vpnUser = data.getString("vpn_user");
            task.vpnPass = data.getString("vpn_pass");
            task.oxygenUser = data.getString("oxygen_user");
            task.oxygenPass = data.getString("oxygen_pass");
            task.app_start_time = data.getInt("oxygen_start_time");
            task.vpn_connect_time = data.getInt("vpn_connect_time");
            task.keyword = data.getString("keyword");
            task.page = HttpDaemon.page;
            task.percent = HttpDaemon.percent;
            task.thumbup = HttpDaemon.thumbup;
            task.page_remain = task.page;
            task.thumbup_remain = task.thumbup;
            task.ip = data.getString("ip");
            HttpDaemon.task = task;
            response("ok,ip:" + task.ip + ",host:" + task.vpnServer + ",user:" + task.oxygenUser, 0);
            HttpDaemon.task_index++;
        }
    }


    private void next_step_vpn_start() throws JSONException {
        int s = isVpnConnected();
        switch (s) {
            case 0:
                next("vpn is already connected.", 1);
                break;
            case 1:
                next("vpn is connecting.", 1);
                break;
            default:
                String cmd = "mtpd rmnet_data0 pptp " + task.vpnServer + " 1723 name " + task.vpnUser + " password " + task.vpnPass + " linkname vpn refuse-eap nodefaultroute idle 1800 mtu 1400 mru 1400 nomppe unit 0 &";
                System.out.println(cmd);
                run(cmd);
                next("cmd is run", 1);
                break;
        }
        task.current_step = UiViewIdConst.NEXT_STEP_VPN_CHECK;
    }

    private void next_step_vpn_check() throws JSONException {
        switch (isVpnConnected()) {
            case 0:
                run("ip ro add default dev ppp0 table 0x3c");
                task.current_step = UiViewIdConst.NEXT_STEP_OXYGEN_START;
                next(task.current_step, 1);
                break;
            case 1:
                //这个地方不能直接结束，因为PPPD或者MTPD进程还在运行，要结束它们
                task.current_step = UiViewIdConst.NEXT_STEP_VPN_STOP;
                next("VPN is connecting,stop and choose another one", task.app_start_time);
                break;
            default:
                next("vpn is lost connection.renew a task", -1);
                break;
        }
    }

    /**
     * 0 已连接
     * 1 正在连接
     * 2 连接失败
     *
     * @return int
     */
    private int isVpnConnected() {
        String r = run("ps | grep mtpd");
        String q = run("ifconfig | grep ppp0");
        if (!r.isEmpty() && !q.isEmpty()) {
            return 0;
        }
        if (!r.isEmpty()) {
            return 1;
        }
        return 2;
    }

    private void next_step_vpn_stop() throws JSONException {
        run("killall -15 mtpd");
        next("VPN已断开", -1);
        //run("killall -9 mtpd pppd");
    }

    private void next_step_oxygen_start(UiAutomation uiAutomation) throws JSONException, RemoteException {
        AccessibilityNodeInfo node, root = uiAutomation.getRootInActiveWindow();
        AccessibilityNodeInfo n1, n2;

        if (task.page_remain <= 0) {
            task.current_step = UiViewIdConst.NEXT_STEP_OXYGEN_STOP;
            next("准备退出", 2);
            return;
        }

        if (root == null) {
            uiAutomation.disconnect();
            uiAutomation.connect();
            next("uiAutomation出错", 2);
            return;
        }

        if (!root.getPackageName().equals(UiViewIdConst.APP_PACKAGE_NAME)) {
            next("App没有启动，先启动", task.app_start_time);
            startOxygen();
            return;
        }

        String ta = getTopActivity();
        //有广告，关广告
        node = getById(root, UiViewIdConst.ID_APP_AD_CLOSE);
        if (node != null) {
            performClick(node);
            next("发现广告，关闭广告", 2);
            return;
        }
        //是否在搜索页面
        if (ta.equals(UiViewIdConst.ACTIVITY_DIARY_CONTENT)) {
            //日记内容页
            task.indexOfPage++;
            //根据实际情况，这个地方要重连下UIAUTOMATOR
            run("input keyevent 4");
            next("日记内容页，返回", 2);
            uiAutomation.disconnect();
            uiAutomation.connect();
            return;
        } else if (ta.equals(UiViewIdConst.ACTIVITY_DIARY_MAIN)) {
            //日记页
            node = getByText(root, UiViewIdConst.TEXT_DIARY_TOP_TITLE);
            if (node != null) {
                if (node.getViewIdResourceName().equals(UiViewIdConst.ID_DIARY_TOP_TITLE)) {
                    //next("在日记页，准备操作，还没有写。。。", 2);
                    node = getById(root, UiViewIdConst.ID_DIARY_CONTENT_LIST);
                    if (node == null) {
                        next("在日记页没有找到列表", 2);
                        return;
                    }

                    //comment
                    List<AccessibilityNodeInfo> list1 = root.findAccessibilityNodeInfosByViewId(UiViewIdConst.ID_DIARY_COUNT);

                    //like
                    List<AccessibilityNodeInfo> list2 = root.findAccessibilityNodeInfosByViewId(UiViewIdConst.ID_DIARY_LIKE);
                    if (list1.size() != list2.size()) {
                        next("评论和点赞按钮个数不相等", 2);
                        return;
                    }

                    if (list2.size() == 0) {
                        task.indexOfPage = 0;
                        //向下翻页
                        node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                        next("在日记页,当前页没有找到点赞，向下翻页", 2);
                        return;
                    }

                    //删除不可见元素
                    if (!list2.get(0).isVisibleToUser()) {
                        list1.remove(0);
                        list2.remove(0);
                    }
                    int len = list2.size();
                    if (len > 0) {
                        if (!list2.get(len - 1).isVisibleToUser()) {
                            list1.remove(len - 1);
                            list2.remove(len - 1);
                        }
                    }


                    if (list2.size() <= task.indexOfPage) {
                        task.page_remain--;
                        //向下翻页
                        node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                        next("在日记页,当前页已点完" + (task.indexOfPage) + "/" + (list2.size()) + "，向下翻页(" + (task.page - task.page_remain) + "/" + task.page + ")", 2);
                        task.indexOfPage = 0;
                        return;
                    }

                    //随机一个数和percent比较，小，点,大,task.indexOfPage++
                    Random random = new Random();
                    int r = random.nextInt(100);
                    if (r < task.percent) {
                        //点赞
                        next("在日记页,点赞,并进入日记内容页" + (task.indexOfPage) + "/" + (list2.size()) + ",当前页面进度(" + (task.page - task.page_remain) + "/" + task.page + ")", 2);
                        if (task.thumbup_remain > 0) {
                            performClick(list2.get(task.indexOfPage));
                            sleep(3);
                        }

                        //进入日记内容页
                        performClick(list1.get(task.indexOfPage));

                        return;
                    } else {
                        task.indexOfPage++;
                        next("在日记页,随机值大于percent，跳过这条日记" + (task.indexOfPage) + "/" + (list2.size()) + ",当前页面进度(" + (task.page - task.page_remain) + "/" + task.page + ")", 2);
                        return;
                    }
                } else {
                    next("比较顶部标题，匹配失败,找到的标题是:" + node.getViewIdResourceName(), 2);
                    return;
                }
            }
            //不在日记页，返回
            run("input keyevent 4");
            next("不在日记页，返回", 2);
            return;
        } else if (ta.equals(UiViewIdConst.ACTIVITY_HOSPITAL_MAIN)) {
            //医院首页
            node = getById(root, UiViewIdConst.ID_HOSPITAL_MAIN_LIST);
            if (node == null) {
                next("在医院首页没有找到LIST，没法向下翻页", 2);
                return;
            }
            int c = 5;//向上翻5页找677案例,正常情况下在第一页
            while (c > 0) {
                n1 = getById(root, UiViewIdConst.ID_DIARY_BTN);
                if (n1 != null) {
                    performClick(n1);
                    task.indexOfPage = 0;
                    next("找到精华日记，点击进入日记页", 2);
                    return;
                }
                node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                sleep(3);
                c--;
            }
            next("向上翻了5页,没有找到精华日记", 2);
            return;
        } else {
            if (task.logined) {
                if (ta.equals(UiViewIdConst.ACTIVITY_MAIN_SEARCH)) {
                    node = getById(root, UiViewIdConst.ID_SEARCH_INPUT_IN_SEARCH);
                    if (node == null) {
                        next("在搜索页面没有找到输入框", 2);
                        return;
                    }
                    pasteText(node, task.keyword);
                    sleep(3);
                    node = getByText(root, UiViewIdConst.TEXT_BTN_RESULT);
                    if (node != null) {
                        performClick(node, true);
                        next("查找到结果点击", 2);
                        return;
                    } else {
                        next("没有找到 上海南浦妇科医院 （私密整形养护中心）", 2);
                        return;
                    }
                } else if (ta.equals(UiViewIdConst.ACTIVITY_HOME)) {
                    node = getById(root, UiViewIdConst.ID_SEARCH_INPUT_IN_MAIN);
                    if (node == null) {
                        node = getById(root, UiViewIdConst.ID_HOME_BTN);
                        if (node == null) {
                            next("在首页找不到底部首页按钮", 2);
                            return;
                        }
                        performClick(node, true);
                        next("登陆成功，点击底部首页按钮", 2);
                        return;
                    }
                    //pasteText(node,task.keyword);
//                        node = getById(root,UiViewIdConst.ID_HOME_BTN);
                    performClick(node, true);
                    next("点击顶部搜索输入框", 2);
                    return;
                } else {
                    next("未知窗口(" + ta + ")", 2);
                    return;
                }
            }

            node = getById(root, UiViewIdConst.ID_USER_LEVEL);
            if (node != null) {
                task.logined = true;
                next("已登陆，准备搜索", 2);
                return;
            }

            //没有登陆
            switch (task.oxygen_login_step) {
                case "init":
                    if (ta.equals(UiViewIdConst.ACTIVITY_MY_LOGIN)) {
                        task.oxygen_login_step = "my+";
                        next("准备使用用户名和密码登陆", 2);
                        return;
                    } else if (ta.equals(UiViewIdConst.ACTIVITY_HOME)) {
                        node = getById(root, UiViewIdConst.ID_MY_BTN);
                        Rect rect = node.getBoundsInScreen();
                        int left, top;
                        left = rect.left + 2;
                        top = rect.top + 2;
                        System.out.println("my button left" + left + ",top:" + top);
                        if (node == null) {
                            next("在首页找不到 我的 按钮", 10);
                            return;
                        }
                        performClick(node, true);
                        task.oxygen_login_step = "my-";
                        next("点击【我的】按钮", 2);
                        return;
                    } else {
                        next("未知窗口(ACTIVITY:" + ta + ")", 2);
                        return;
                    }
                case "my-":
                    if (ta.equals(UiViewIdConst.ACTIVITY_MY_LOGIN)) {
                        task.oxygen_login_step = "my+";
                        next("准备使用用户名和密码登陆", 2);
                        return;
                    } else if (ta.equals(UiViewIdConst.ACTIVITY_HOME)) {
                        task.oxygen_login_step = "init";
                        next("找不到我的页面按钮", 2);
                        return;
                    } else {
                        next("未知窗口(ACTIVITY:" + ta + ")", 2);
                        return;
                    }
                case "my+":
                    if (ta.equals(UiViewIdConst.ACTIVITY_MY_LOGIN)) {
                        task.oxygen_login_step = "my+";

                        node = getById(root, UiViewIdConst.ID_LOGIN_VC);
                        n1 = getById(root, UiViewIdConst.ID_LOGIN_USER);
                        n2 = getById(root, UiViewIdConst.ID_LOGIN_PASS);
                        if (node != null && node.isVisibleToUser()) {
                            node = getByText(root, "账号密码登录");
                            if (node == null) {
                                next("在登陆页面找不到：账号密码登录", 10);
                                return;
                            }
                            task.oxygen_login_step = "login-";
                            performClick(node);
                            next("准备使用用户名和密码登陆", 2);
                            return;
                        } else if (n1 != null && n2 != null) {
                            task.oxygen_login_step = "login+";
                            next("找不到用户和密码的输入处", 2);
                            return;
                        }
                        return;
                    } else if (ta.equals(UiViewIdConst.ACTIVITY_HOME)) {
                        task.oxygen_login_step = "init";
                        next("点击输入我的页面", 2);
                        return;
                    } else {
                        next("未知窗口(ACTIVITY:" + ta + ")", 2);
                        return;
                    }
                case "login-":
                    if (ta.equals(UiViewIdConst.ACTIVITY_MY_LOGIN)) {
                        n1 = getById(root, UiViewIdConst.ID_LOGIN_USER);
                        n2 = getById(root, UiViewIdConst.ID_LOGIN_PASS);
                        if (n1 != null && n2 != null) {
                            task.oxygen_login_step = "login+";
                            next("准备输入用户名和密码", 2);
                            return;
                        } else {
                            task.oxygen_login_step = "my+";
                            next("没用找到用户名和密码的输入框", 2);
                            return;
                        }
                    } else if (ta.equals(UiViewIdConst.ACTIVITY_HOME)) {
                        task.oxygen_login_step = "init";
                        next("没有进入到我的页面，准备重试", 2);
                        return;
                    } else {
                        next("未知窗口(ACTIVITY:" + ta + ")", 2);
                        return;
                    }
                case "login+":
                    if (ta.equals(UiViewIdConst.ACTIVITY_MY_LOGIN)) {
                        n1 = getById(root, UiViewIdConst.ID_LOGIN_USER);
                        n2 = getById(root, UiViewIdConst.ID_LOGIN_PASS);
                        if (n1 != null && n2 != null) {
                            pasteText(n1, task.oxygenUser);
                            sleep(1);
                            pasteText(n2, task.oxygenPass);
                            sleep(1);
                            node = getById(root, UiViewIdConst.ID_LOGIN_SUBMIT_BTN);
                            if (node == null) {
                                next("在登陆页面找不到：登陆提交按钮", 10);
                                return;
                            }
                            performClick(node);
                            next("准备输入用户名和密码", 2);
                            return;
                        } else {
                            task.oxygen_login_step = "my+";
                            next("没有找到用户名和密码输入框", 2);
                            return;
                        }
                    } else if (ta.equals(UiViewIdConst.ACTIVITY_HOME)) {
                        task.oxygen_login_step = "init";
                        next("没有进入到我的页面，重试", 2);
                        return;
                    } else {
                        next("未知窗口(ACTIVITY:" + ta + ")", 2);
                        return;
                    }
            }

            next("未知错误:" + ta + ")", 2);
        }

    }

    private void sleep(int sec) {
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void next_step_oxygen_stop(UiAutomation uiAutomation) throws JSONException {
        String ta = getTopActivity();
        AccessibilityNodeInfo node, root;
        if (ta.equals(UiViewIdConst.ACTIVITY_DIARY_CONTENT)) {
            //根据实际情况，这个地方要重连下UIAUTOMATOR
            run("input keyevent 4");
            next("准备退出,日记内容页，返回", 2);
            uiAutomation.disconnect();
            uiAutomation.connect();
            return;
        } else if (ta.equals(UiViewIdConst.ACTIVITY_DIARY_MAIN)) {
            run("input keyevent 4");
            next("准备退出,日记首页，返回", 2);
            uiAutomation.disconnect();
            uiAutomation.connect();
            return;
        } else if (ta.equals(UiViewIdConst.ACTIVITY_HOSPITAL_MAIN)) {
            //医院首页
            run("input keyevent 4");
            next("准备退出,医院首页，返回", 2);
            uiAutomation.disconnect();
            uiAutomation.connect();
            return;
        } else if (ta.equals(UiViewIdConst.ACTIVITY_MAIN_SEARCH)) {
            //搜索页面
            run("input keyevent 4");
            next("准备退出,搜索页面，返回", 2);
            uiAutomation.disconnect();
            uiAutomation.connect();
            return;
        } else if (ta.equals(UiViewIdConst.ACTIVITY_HOME)) {
            //首页页面
            root = uiAutomation.getRootInActiveWindow();
            if (root == null) {
                uiAutomation.disconnect();
                uiAutomation.connect();
                next("uiAutomation出错", 2);
                return;
            }
            //是不是在我的页面
            node = getById(root, UiViewIdConst.ID_LOGOUT_SETTING);
            if (node == null) {
                node = getById(root, UiViewIdConst.ID_MY_BTN);
                if (node == null) {
                    next("在HOME页面下找不到 我的 按钮", 2);
                    return;
                }
                performClick(node, true);
                next("点击进入我的页面", 2);
                return;
            }
            performClick(node);
            next("点击进入我的页面", 2);
            return;
        } else if (ta.equals(UiViewIdConst.ACTIVITY_SETTING)) {
            root = uiAutomation.getRootInActiveWindow();
            if (root == null) {
                uiAutomation.disconnect();
                uiAutomation.connect();
                next("uiAutomation出错", 2);
                return;
            }

            node = getByText(root, UiViewIdConst.TEXT_LOGOUT_DIALOG_TEXT);
            if (node != null) {
                if (node.getViewIdResourceName().equals(UiViewIdConst.ID_LOGOUT_DIALOG_TEXT)) {
                    node = getById(root, UiViewIdConst.ID_LOGOUT_DIALOG_OK);
                    if (node == null) {
                        next("在退出确认对话框上找不到确定按钮", 2);
                        return;
                    }
                    performClick(node);
                    next("确认退出", 2);
                    return;
                }
            }

            node = getById(root, UiViewIdConst.ID_LOGOUT_EXIT);
            if (node == null) {
                next("在设置页面下找不到 退出 按钮", 2);
                return;
            }
            performClick(node);
            next("点击退出", 2);
            return;
        } else if (ta.equals(UiViewIdConst.ACTIVITY_MY_LOGIN)) {
            task.current_step = UiViewIdConst.NEXT_STEP_VPN_STOP;
            next("退出完成，准备断开VPN", 2);
            return;
        } else {
            next("未知错误:" + ta + ")", 2);
        }
    }

    private AccessibilityNodeInfo getById(AccessibilityNodeInfo root, String id) {
        List<AccessibilityNodeInfo> l = root.findAccessibilityNodeInfosByViewId(id);
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i).isVisibleToUser()) {
                return l.get(i);
            }
        }

        return null;
    }

    private AccessibilityNodeInfo getByText(AccessibilityNodeInfo root, String text) {
        List<AccessibilityNodeInfo> l = root.findAccessibilityNodeInfosByText(text);
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i).isVisibleToUser()) {
                return l.get(i);
            }
        }
        return null;
    }

    public static String getTopActivity() {
        String r = run("dumpsys activity top | grep ACTIVITY");
        String t[] = r.split(" ");
        if (t.length == 6)
            return t[3];
        return "";
//        ComponentName componentName;
//        if (Build.VERSION.SDK_INT > 19) {
//            componentName = Objects.requireNonNull(getActivityManager()).getTasks(1, 0).get(0).topActivity;
//            return componentName.getClassName();
//        } else {
//            //java.util.List<android.app.ActivityManager.RunningTaskInfo> = in
//            Log.e("Garri", "use reflect invoke ActivityManager.getTasks");
//        }
//        return "use reflect invoke ActivityManager.getTasks";
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
                String cmd = "input tap " + left + " " + top;
                System.out.println(cmd);
                run(cmd);
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

    /**
     * delay -1 表示新任务
     *
     * @param message
     * @param sleep
     * @throws JSONException
     */
    private void next(String message, int sleep) throws JSONException {
        JSONObject cmd = new JSONObject();
        cmd.put("code", 0);
        cmd.put("delay", sleep);
        cmd.put("message", message);
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
        error("Page not found:" + url);
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

    public static void pasteText(AccessibilityNodeInfo info, String text) throws RemoteException {
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
}

