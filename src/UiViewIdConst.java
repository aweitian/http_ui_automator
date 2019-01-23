package ret.sec.oxygenauto.daemon;
//adb shell setprop persist.view.server.force 1

public class UiViewIdConst {
    //dumpsys activity top | grep ACTIVITY
    public final static String APP_PACKAGE_NAME = "com.youxiang.soyoungapp";

    public final static String NEXT_STEP_VPN_START = "vpn_start";
    public final static String NEXT_STEP_VPN_CHECK = "vpn_check";
    public final static String NEXT_STEP_OXYGEN_START = "oxygen_start";
    public final static String NEXT_STEP_OXYGEN_STOP = "oxygen_stop";
    public final static String NEXT_STEP_VPN_STOP = "vpn_stop";

    public final static String ID_APP_AD_CLOSE = "com.youxiang.soyoungapp:id/imgClose";


    public final static String ACTIVITY_SEARCH_RESULT = "com.youxiang.soyoungapp/.main.home.search.SearchIndexActivity";
    public final static String ACTIVITY_HOME = "com.youxiang.soyoungapp/.ui.MainActivity";
    public final static String ACTIVITY_MY_LOGIN = "com.youxiang.soyoungapp/com.soyoung.login_module.login.LoginActivity";
    public final static String ACTIVITY_MY_LOGINED = "com.youxiang.soyoungapp/.ui.MainActivity";
    public final static String ACTIVITY_MY_LOGOUT = "com.youxiang.soyoungapp/com.soyoung.module_setting.SettingActivity";
//    public final static String NEXT_STEP_SLEEP = "sleep";



    //CLIENT端只发NEW_TASK/NEXT/BACK/STOP/DONE






    //ADB 总结:https://mazhuang.org/awesome-adb/#%E6%9F%A5%E7%9C%8B%E5%89%8D%E5%8F%B0-activity

    //NEW OXYGEN 7.16.0

    //验证用于验证APP是否可用
    //根据serialno来获取手机号密码，关键词  getprop ro.serialno


    //手机号密码,搜索关键词保存到bd.52733999.com上

    //action:NEW
    //CLIENT向 SVC提交一个任务,KW,VPN_HOST,VPN_USER,VPN_PASS,THUMBUP,PERCENT,PAGE
    //svc查询当前没有任务在运行，有返回FALSE，没有返回TRUE

    //action:query 查询当前有没有任务，有，返回当前任务进度，没有，返回当前任务已完成


    //SVC有一个当前STEP，每个STEP里面根据界面ID来点击


    //request
    //step:home/vpn_connect/oxygen/close_oxygen/close_vpn
    //forward:next/back

    //home(1)
    //vpn_connect(2) : main/vpn/add/connect
    //oxygen() : main/my/login/
    //

    //response
    //next step:
    //next forward
    //CLIENT端保存当前STEP


    //NEXT函数
        //有搜索按钮就点搜索按钮                    next
        //有搜索输入框就点搜索输入框                 next
        //有查找微信号:xxxxxxxxxx按钮就点这个按钮     next
        //如果找不到，点击确定                      BACK
        //如果找到，点击验证按钮进入验证页面          next
        //如果是验证页面，点击发送按钮                back
        //以上都不是                                 back
    //BACK To HOME函数
        //一直按返回直到出现搜索按钮
        //如果顶部不是WEIXIN，启动微信

}
