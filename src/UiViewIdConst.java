package ret.sec.oxygenauto.daemon;
//adb shell setprop persist.view.server.force 1

public class UiViewIdConst {
    //接受sn 和  i 参数（GET），返回JSON
    public final static String TASK_FEED_URL = "https://xcx.sh9l.com/oxygen_auto_verify/getTaskInfo.php";
    //   dumpsys activity top | grep ACTIVITY
    //   curl http://127.0.0.1:3820/next
    //   getprop ro.serialno
    public final static String APP_PACKAGE_NAME = "com.youxiang.soyoungapp";

    public final static String NEXT_STEP_VPN_START = "vpn_start";
    public final static String NEXT_STEP_VPN_CHECK = "vpn_check";
    public final static String NEXT_STEP_OXYGEN_START = "oxygen_start";
    public final static String NEXT_STEP_OXYGEN_STOP = "oxygen_stop";
    public final static String NEXT_STEP_VPN_STOP = "vpn_stop";

    public final static String ID_APP_AD_CLOSE = "com.youxiang.soyoungapp:id/imgClose";
    public final static String ID_LOGIN_USER = "com.youxiang.soyoungapp:id/user_name";
    public final static String ID_LOGIN_PASS = "com.youxiang.soyoungapp:id/pwd";
    public final static String ID_LOGIN_VC = "com.youxiang.soyoungapp:id/code_num";
    public final static String ID_LOGIN_SUBMIT_BTN = "com.youxiang.soyoungapp:id/bt_login";
    public final static String ID_MY_BTN = "com.youxiang.soyoungapp:id/main_my_center";
    public final static String ID_HOME_BTN = "com.youxiang.soyoungapp:id/main_home";
    public final static String ID_DIARY_BTN = "com.youxiang.soyoungapp:id/hospital_detail_info_sample";//677案例
    public final static String ID_DIARY_TOP_TITLE = "com.youxiang.soyoungapp:id/top_center_title";//需要结合TEXT来确定唯一性
    public final static String ID_HOSPITAL_MAIN_LIST = "com.youxiang.soyoungapp:id/recycler_view";//需要结合TEXT来确定唯一性

    //用户级别，用于作为用户是否登陆的标志
    public final static String ID_USER_LEVEL = "com.youxiang.soyoungapp:id/user_level";
    public final static String ID_SEARCH_INPUT_IN_MAIN = "com.youxiang.soyoungapp:id/title_search";
    public final static String ID_SEARCH_INPUT_IN_SEARCH = "com.youxiang.soyoungapp:id/edSearch";

    public final static String ID_DIARY_COUNT = "com.youxiang.soyoungapp:id/view_cnt";
    public final static String ID_DIARY_LIKE = "com.youxiang.soyoungapp:id/like_cnt";
    public final static String ID_DIARY_CONTENT_LIST = "com.youxiang.soyoungapp:id/list";
    public final static String ID_LOGOUT_SETTING = "com.youxiang.soyoungapp:id/setting";
    public final static String ID_LOGOUT_EXIT = "com.youxiang.soyoungapp:id/exit";
    public final static String ID_LOGOUT_DIALOG_TEXT = "android:id/message";
    public final static String ID_LOGOUT_DIALOG_OK = "android:id/button1";
    public final static String ID_DIARY_DETAIL_ID = "com.youxiang.soyoungapp:id/diary_num";//日记变美过程篇数，2018-07-10创建  共9篇日记
    public final static String ID_DIARY_DETAIL_CONTENT = "com.youxiang.soyoungapp:id/content";//点击这个增加浏览量
    public final static String ID_DIARY_DETAIL_LIST = "com.youxiang.soyoungapp:id/recyclerView";//点击这个增加浏览量
    public final static String ID_DISCOVER = "com.youxiang.soyoungapp:id/main_discover";//点击这个增加浏览量


    public final static String TEXT_BTN_RESULT = "上海南浦妇科医院 （私密整形养护中心）";
    public final static String TEXT_DIARY_TOP_TITLE = "医院的案例";
    public final static String TEXT_LOGOUT_DIALOG_TEXT = "确定要退出当前账号？";
    public final static String TEXT_DIARY_DETAIL_FIRST = "第1篇日记";


    public final static String ACTIVITY_HOME = "com.youxiang.soyoungapp/.ui.MainActivity";
    //com.youxiang.soyoungapp/com.soyoung.login_module.login.LoginActivity
    public final static String ACTIVITY_MY_LOGIN = "com.youxiang.soyoungapp/com.soyoung.login_module.login.LoginActivity";
    public final static String ACTIVITY_MAIN_SEARCH = "com.youxiang.soyoungapp/.main.home.search.SearchMainActivity";
    public final static String ACTIVITY_HOSPITAL_MAIN = "com.youxiang.soyoungapp/.main.mine.hospital.view.HospitalDetailActivity";
    public final static String ACTIVITY_DIARY_MAIN = "com.youxiang.soyoungapp/.main.mine.doctor.CommonListActivity";
    public final static String ACTIVITY_DIARY_CONTENT = "com.youxiang.soyoungapp/.ui.main.DiaryModelActivity";
    public final static String ACTIVITY_SETTING = "com.youxiang.soyoungapp/com.soyoung.module_setting.SettingActivity";
    public final static String ACTIVITY_DIARY_DETAIL_CONTENT = "com.youxiang.soyoungapp/.main.BeautyContentNewActivity";


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
