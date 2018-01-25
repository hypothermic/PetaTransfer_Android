package nl.hypothermic.android.petatransfer;

import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by hypothermic on 1/22/2018.
 * @author hypothermic
 * https://hypothermic.nl
 * https://github.com/hypothermic
 */

public class pttPSender extends hoofdvenster {

    private static TextView logView;

    public pttPSender(TextView x) {
        logView = x;
    }


    private void xlog(final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logView.append(value);
            }
        });
    }

    public void send(int portNo,String fileLocation) throws IOException {
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;

        OutputStream outputStream = null;
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(portNo);
            xlog("\n[SERVER] Waiting for receiver...");
            try {
                socket = serverSocket.accept();
                xlog("\n[SERVER] Accepted connection: " + socket);

                // bytearray ~= 2GB
                long allocatedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
                long freeMemory = Runtime.getRuntime().maxMemory() - allocatedMemory;
                File file = new File(fileLocation);
                if (!file.exists() || file.isDirectory()) {
                    xlog("[SERVER] File does not exist. Exiting.");
                }
                if (freeMemory < (int) file.length()) {
                    xlog("[SERVER] Not enough memory availible. Exiting.");
                    return;
                }
                long startTime = System.currentTimeMillis();
                // file -> bytearray
                byte[] byteArray = new byte[(int) file.length()];
                fileInputStream = new FileInputStream(file);
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                bufferedInputStream.read(byteArray, 0, byteArray.length); // copied file into byteArray

                outputStream = socket.getOutputStream();
                xlog("\n[SERVER] Transferring \'" + fileLocation + "\', total of \'" + byteArray.length + "\' bytes.");
                outputStream.write(byteArray, 0, byteArray.length);
                outputStream.flush();
                long stopTime = System.currentTimeMillis();
                xlog("\n[SERVER] Done transferring file, total time: " + ((stopTime - startTime) / 1000) + "s");
                byteArray = null;
                System.gc();
                srvRunning = 0;
            } finally {
                if (bufferedInputStream != null) bufferedInputStream.close();
                if (outputStream != null) bufferedInputStream.close();
                if (socket != null) socket.close();
                srvRunning = 0;
            }
        } catch (IOException x) {
            // TODO Auto-generated catch block
            x.printStackTrace();
            xlog("\n[ERR] IOException: Connection to client has been lost: " + x);
            srvRunning = 0;
        } finally {
            if (serverSocket != null) serverSocket.close();
            srvRunning = 0;
        }
    }
}
