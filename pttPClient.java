package nl.hypothermic.android.petatransfer;

import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by hypothermic on 1/22/2018.
 * @author hypothermic
 * https://hypothermic.nl
 * https://github.com/hypothermic
 */

public class pttPClient extends hoofdvenster{

    private static TextView logView;

    public pttPClient(TextView x) {
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
    public void receiveFile(String ipAddress,int portNo,String fileLocation) throws IOException {
        int bytesRead=0;
        int current = 0;
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        Socket socket = null;
        try {
            long allocatedMemory = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
            long freeMemory = Runtime.getRuntime().maxMemory() - allocatedMemory;
            if (freeMemory < 2000000000) {
                xlog("[CLIENT] Not enough memory availible. Exiting.");
                return;
            }
            socket = new Socket(ipAddress,portNo);
            xlog("\n[CLIENT] Connected to " + ipAddress);

            byte [] byteArray  = new byte [2000000000]; //6022386 ~= 6MB, 2000000000 ~= 2GB
            xlog("\n[CLIENT] Downloading file");
            InputStream inputStream = socket.getInputStream();
            fileOutputStream = new FileOutputStream(fileLocation);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bytesRead = inputStream.read(byteArray,0,byteArray.length);
            xlog("\n[CLIENT] Retrieved file from socket, now writing to file.");
            current = bytesRead;
            do {
                bytesRead =inputStream.read(byteArray, current, (byteArray.length-current));
                if(bytesRead >= 0) current += bytesRead;
            } while(bytesRead > -1);
            xlog("\n[CLIENT] Writing..");
            bufferedOutputStream.write(byteArray, 0 , current);
            bufferedOutputStream.flush();
            // ram cleanup
            byteArray = null;
            System.gc();
            xlog("\n[CLIENT] Content saved as \'" + fileLocation  + "\', total of \'" + pttPFormatSize.formatDecimaal(current) + "\'");
        } catch (UnknownHostException xh) {
            xlog("[CLIENT] Exception: could not reach server");
        } catch (ConnectException xc) {
            xlog("[CLIENT] Exception: timeout while trying to reach server");
        } catch (IOException e) {
            xlog(e.toString() + e.getMessage());
        }
        finally {
            if (fileOutputStream != null) fileOutputStream.close();
            if (bufferedOutputStream != null) bufferedOutputStream.close();
            if (socket != null) socket.close();
            clRunning = 0;
        }
    }
}