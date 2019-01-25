package ret.sec.oxygenauto.daemon;

public class Task {
    public int thumbup;
    public int percent;//每篇日记随机一个0-100的数，数小于这个percent就点击
    public int page;


    public String vpnServer;
    public String vpnUser;
    public String vpnPass;
    public String oxygenPass;
    public String oxygenUser;
    public String keyword;
    public int app_start_time;
    public int vpn_connect_time;

    public String ip;

    public String current_step;

    // 负状态只检查,切换，不点击
    public String oxygen_login_step = "init";//my- / my+ / login- / login+
    public boolean logined = false;
    public int page_remain;
    public int thumbup_remain;
    public int indexOfPage = 0;
    public int indexOfDiary = 0;        //一个大日记下第几篇小日记
}
