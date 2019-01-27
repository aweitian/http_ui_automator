package ret.sec.oxygenauto.daemon;

public class Task {
    public final static int default_foundDetailFirstCountTTL = 10;
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
    public String oxygen_login_step = "init";//my- / my+ / login- / login+ / check
    public boolean logined = false;
    public int page_remain;
    public int thumbup_remain;
    public int indexOfPage = 0;

    public int ttl_login_wait_time = 5;             //在 check中检查，等于0重新来过

    public boolean foundDetailFirst = false;        //是否找到第一篇
    public int foundDetailFirstCountTTL = default_foundDetailFirstCountTTL;           //向下翻10页没有找到也就停止
    public boolean isClickOpOfDetail = true;              //在变美过程页面是点击还是向下翻页
    public void resetDetail() {
        foundDetailFirst = false;
        foundDetailFirstCountTTL = default_foundDetailFirstCountTTL;
        isClickOpOfDetail = true;
    }


}
