//package zsh.jl.zshui;
package ret.sec.oxygenauto;

public class Daemon {
    public static void main(String[] args) {
        try {
            if (args.length == 1) {
                new Daemon(Integer.parseInt(args[0]));
            } else {
                showUsage();
            }
        } catch (IllegalArgumentException e) {

            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private Daemon(int port) {
        HttpDaemon httpDaemon = new HttpDaemon(port);
        httpDaemon.
    }

    private void showUsage() {

    }
}

