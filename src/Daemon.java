//package zsh.jl.zshui;
package ret.sec.oxygenauto.daemon;

import android.app.UiAutomation;
import android.app.UiAutomationConnection;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.view.accessibility.AccessibilityNodeInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.List;

public class Daemon {
    private static final String HANDLER_THREAD_NAME = "UiAutomatorHandlerThread";
    private static final HandlerThread mHandlerThread = new HandlerThread(HANDLER_THREAD_NAME);
    public static UiAutomation mUiAutomation;

    public static void main(String[] args) {
        try {
            if (args.length == 4) {
                new Daemon(
                        Integer.parseInt(args[0]),
                        Integer.parseInt(args[1]),
                        Integer.parseInt(args[2]),
                        Integer.parseInt(args[3])
                );
            } else {
                showUsage();
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }


    private Daemon(int thumbup, int percent, int page, int port) {
        if (!mHandlerThread.isAlive()) {
            mHandlerThread.start();
        }

        mUiAutomation = new UiAutomation(mHandlerThread.getLooper(),
                new UiAutomationConnection());
        mUiAutomation.connect();
        System.out.println("disableInput();");
        disableInput();
        System.out.println("addGlobalRoute();");
        addGlobalRoute();
        new HttpDaemon(thumbup, percent, page, port);
        System.out.println("rmGlobalRoute();");
        rmGlobalRoute();
        System.out.println("enableInput();");
        enableInput();
        mUiAutomation.disconnect();
        System.out.println("mHandlerThread.quit();");
        mHandlerThread.quit();
    }

    private void addGlobalRoute() {
        CmdHandle.run("ip ru add from all lookup 60");
    }

    private void rmGlobalRoute() {
        String cmd = "ip ru show | grep \"from all lookup 60\"";
        String r = CmdHandle.run(cmd);
        while (!r.isEmpty()) {
            CmdHandle.run("ip ru del from all lookup 60");
            r = CmdHandle.run(cmd);
        }
    }

    private int disableInput() {
        String ori_inp = CmdHandle.run("ime list -s");
        int i = 0;
        String ois[] = ori_inp.split("\n");
        for (String input : ois) {
            if (!input.isEmpty()) {
                CmdHandle.run("ime disable " + input);
                i++;
            }
        }
        if (!ori_inp.isEmpty())
            util.filePutContents("/data/data/ret_sec_oxygenauto_daemon_input.txt", ori_inp);
        return i;
    }

    private int enableInput() {
        String ori_inp = util.fileGetContents("/data/data/ret_sec_oxygenauto_daemon_input.txt");
        if (ori_inp == null) {
            return 0;
        }
        int i = 0;
        String ois[] = ori_inp.split("\n");
        for (String input : ois) {
            if (!input.isEmpty()) {
                CmdHandle.run("ime enable " + input);
                i++;
            }
        }
        return i;
    }

    private static void showUsage() {
        System.err.println("Daemon thumbup percent page port");
    }
}

