package ret.sec.oxygenauto.daemon;

public class UiViewIdConst {

    public static String NEXT_STEP_VPN_START = "vpn_start";
    public static String NEXT_STEP_OXYGEN_START = "oxygen_start";
    public static String NEXT_STEP_OXYGEN_STOP = "oxygen_stop";
    public static String NEXT_STEP_VPN_STOP = "vpn_stop";
    public static String NEXT_STEP_SLEEP = "sleep";









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

    //搜索按钮查找方法:先找到顶部黑色BAR，然后它的第1孩子的第0个孩子结点,如果这个孩子结点的content-desc等于搜索就认为找到
    //public static String HOME_TOP_BAR_ID = "com.tencent.mm:id/ij";//整个顶部黑色BAR 1/0是搜索按钮
    public static String HOME_FIRST_BTN_ID = "com.tencent.mm:id/qr";//首页底部第一个按钮(微信)   android.widget.ImageView
    public static String HOME_SEARCH_BTN_ID = "com.tencent.mm:id/ij";//首页搜索按钮  android.widget.ImageView
    public static String SEARCH_TOP_EDIT_INPUT_ID = "com.tencent.mm:id/ka";//搜索页面的搜索输入框  android.widget.EditText
    public static String SEARCH_RESULT_BTN_ID = "com.tencent.mm:id/bvf";//查找手机/QQ号:13764769496 android.widget.TextView
    public static String SEARCH_NOT_FOUND_TEXTVIEW_ID = "com.tencent.mm:id/d3f";//用户不存在文字，如果存在，表示没有搜索到结果 android.widget.TextView
    public static String SEARCH_CONTACTINFOUI_BTN_ID = "com.tencent.mm:id/cp";//添加到通讯录按钮 联系人页面 android.widget.TextView
    public static String SAY_HI_WITH_SNS_PERMISSIONUI_BTN_ID = "com.tencent.mm:id/jq";//发送按钮 验证页面 android.widget.Button


    public static String ACTIVITY_NAME_MAIN = ".ui.LauncherUI";                 //主页面
    public static String ACTIVITY_NAME_SEARCH = ".plugin.fts.ui.FTSMainUI";       //搜索页面
    public static String ACTIVITY_NAME_CONTACT = ".plugin.profile.ui.ContactInfoUI";       //添加联系人页面.plugin.profile.ui.ContactInfoUI
    public static String ACTIVITY_NAME_VERIFY = ".plugin.profile.ui.SayHiWithSnsPermissionUI";       //发送验证页面


    public static String RESPONSE_RESULT_WX_NEXT = "next";
    public static String RESPONSE_RESULT_WX_BACK = "back";
    public static String RESPONSE_RESULT_WX_STOP = "stop";
    public static String RESPONSE_RESULT_WX_DONE = "done";
    public static String RESPONSE_RESULT_WX_START = "start";

}
