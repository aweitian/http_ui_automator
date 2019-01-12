//package zsh.jl.zshui;
package ret.sec.oxygenauto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

import okhttp3.mockwebserver.MockResponse;


public class CmdHandle {
    public void handle(MockResponse remotePeer, JSONObject cmd) {
        response(remotePeer, cmd);
    }

    private void response(MockResponse r, JSONObject cmd) {
        Log.d("Garri", cmd.toString());
        r.setBody(cmd.toString());
    }

    private void unknowCmd(MockResponse remotePeer, String text) {
        JSONObject cmd = new JSONObject();
        try {
            cmd.put("code", 1);
            cmd.put("message", "unknown cmd:" + text);
            response(remotePeer, cmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

