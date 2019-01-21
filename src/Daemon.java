//package zsh.jl.zshui;
package ret.sec.oxygenauto.daemon;

public class Daemon {
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
        disableInput();
        new HttpDaemon(thumbup, percent, page, port);
        enableInput();
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

