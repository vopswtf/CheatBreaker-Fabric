package cc.vops.cheatbreaker.client.websocket.server;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.util.ByteBufWrapper;
import cc.vops.cheatbreaker.client.websocket.AssetsWebSocket;
import cc.vops.cheatbreaker.client.websocket.WSPacket;
import cc.vops.cheatbreaker.client.websocket.client.WSPacketClientProcessList;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Collections;

// CBWSProcessListPacket
public class WSPacketRequestProcessList
        extends WSPacket {
    @Override
    public void write(ByteBufWrapper lIlIllllllllIlIIIllIIllII2) {
    }

    @Override
    public void read(ByteBufWrapper lIlIllllllllIlIIIllIIllII2) {

    }

    @Override
    public void handle(AssetsWebSocket lIIlllIIlllIlIllIIlIIIIll2) {
//        try {
//            String string;
//            Process object = this.getProcesses();
//            InputStream inputStream = object.getInputStream();
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//            while ((string = bufferedReader.readLine()) != null) {
//                CheatBreaker.getInstance().getAssetsWebSocket().send(new WSPacketClientProcessList(Collections.singletonList(string)));
//            }
//            bufferedReader.close();
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }
    }

//    @SneakyThrows
//    private Process getProcesses() {
////        return Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\tasklist.exe");
//    }
}