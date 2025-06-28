import xyz.sqlskid.skidchat.SkidChatServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Start {

    public static void main(String[] args){
        List<String> params = new ArrayList<>();
        for (String arg : args) {
          if (arg.startsWith("-") && arg.length() > 1) {
                params.add(arg.substring(1));
            }
        }

        SkidChatServer skidChatServer = new SkidChatServer(params);
        skidChatServer.start();


    }

}
