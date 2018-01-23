package nl.hypothermic.android.petatransfer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by MAIN on 1/22/2018.
 * @author MAIN
 *
 * not done yet.
 */

public class pttPSender extends hoofdvenster{
    public static void send(int portNo,String fileLocation) throws IOException {
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;

        OutputStream outputStream = null;
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(portNo);
            hoofdvenster.log("\n[SERVER] Waiting for receiver...");
            try {
                socket = serverSocket.accept();
                hoofdvenster.log("\n[SERVER] Accepted connection : " + socket);

                // bytearray ~= 2GB
                long allocatedMemory = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
                long freeMemory = Runtime.getRuntime().maxMemory() - allocatedMemory;
                File file = new File (fileLocation);
                if (freeMemory < (int)file.length()) {
                    hoofdvenster.log("[SERVER] Not enough memory availible. Exiting.");
                    return;
                }
                long startTime = System.currentTimeMillis();
                // file -> bytearray
                byte [] byteArray  = new byte [(int)file.length()];
                fileInputStream = new FileInputStream(file);
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                bufferedInputStream.read(byteArray,0,byteArray.length); // copied file into byteArray

                outputStream = socket.getOutputStream();
                hoofdvenster.log("\n[SERVER] Transferring \'" + fileLocation + "\', total of \'" + byteArray.length + "\' bytes.");
                outputStream.write(byteArray,0,byteArray.length);			//copying byteArray to socket
                outputStream.flush();										//flushing socket
                long stopTime = System.currentTimeMillis();
                hoofdvenster.log("\n[SERVER] Done transferring file, total time: " + ((stopTime - startTime) / 1000) + "s");	//file sent
                srvRunning = 0;
            }
            finally {
                if (bufferedInputStream != null) bufferedInputStream.close();
                if (outputStream != null) bufferedInputStream.close();
                if (socket!=null) socket.close();
                srvRunning = 0;
            }
        } catch (IOException x) {
            // TODO Auto-generated catch block
            x.printStackTrace();
            hoofdvenster.log("\n[ERR] IOException: Connection to client has been lost: " + x);
            srvRunning = 0;
        }
        finally {
            if (serverSocket != null) serverSocket.close();
            srvRunning = 0;
        }
    }
}
