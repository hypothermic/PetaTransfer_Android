package nl.hypothermic.android.petatransfer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by hypothermic on 1/21/2018.
 * @author hypothermic
 * https://hypothermic.nl
 * https://github.com/hypothermic
 */

public class hoofdvenster extends AppCompatActivity {

    protected Button sendButton;
    protected static TextView logField;
    protected EditText sendPortField;
    protected EditText sendFilePathField;
    protected static int srvRunning = 0;
    protected static int clRunning = 0;
    protected EditText saveAsField;
    protected Button rqpButton;
    public String sdLoc = System.getenv("EXTERNAL_STORAGE");
    protected EditText remoteAddrField;
    protected EditText remotePortField;
    protected Button receiveButton;
    protected EditText byteArraySizeField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*TEMP: om android.os.NetworkOnMainThreadException te voorkomen*/
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // dalvik activity create + init vars
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoofdvenster);
        byteArraySizeField = findViewById(R.id.byteArraySizeField);
        byteArraySizeField.setText("104857600");
        sendButton = findViewById(R.id.sendButton);
        logField = findViewById(R.id.logField);
        logField.setMovementMethod(new ScrollingMovementMethod());
        sendPortField = findViewById(R.id.sendPortField);
        sendPortField.setText("9070");
        sendFilePathField = findViewById(R.id.sendFilePathField);
        sendFilePathField.setText(sdLoc);
        remoteAddrField = findViewById(R.id.remoteAddrField);
        remotePortField = findViewById(R.id.remotePortField);
        saveAsField = findViewById(R.id.saveAsField);
        saveAsField.setText(sdLoc + "/x");
        rqpButton = findViewById(R.id.rqPerms);
        receiveButton = findViewById(R.id.receiveButton);

        rqpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                logField.append("\n>> Requesting R+W permissions");
                requestPermission();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (srvRunning == 1) { logField.append("\n[INFO] Cannot send file, process already running!"); return; } else { srvRunning = 1; }
                int port = 9070; String path = null;
                try {
                    port = Integer.parseInt(sendPortField.getText().toString());
                    path = sendFilePathField.getText().toString();
                } catch (Exception x) {
                    logField.append("\n[CRIITICAL] Error: " + x + " at \n" + x.getMessage() + "\n\nExiting!\n\n"); return;
                }
                if (true /*TODO: file check, etc.*/) {
                    Toast.makeText(getApplicationContext(), "sending...", Toast.LENGTH_LONG).show();
                    logField.append("\nsending..");
                    try {
                        final pttPSender x = new pttPSender(logField); final int xport = port; final String xpath = path;
                        new Thread() {
                            public void run() {
                                try {
                                    x.send(xport, xpath);
                                } catch (Throwable x) {
                                    x.printStackTrace();
                                    srvRunning = 0;

                                    logField.append("[CRITICAL] Error in pttPSender: " + x + " at \n" + x.getMessage());
                                }
                            }
                        }.start();
                        logField.append("\n[DEBUG] Exited pttPSender ");
                    } catch (Throwable x) {
                        x.printStackTrace();
                        logField.append("\n[CRITICAL] Exception in pttPSender: " + x + " at \n" + x.getMessage());
                    }
                }

            }
        });
        receiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clRunning == 1) { logField.append("\n[INFO] Cannot receive file, process already running!"); return; } else { clRunning = 1; }
                String addr = null; int port = 9070; String saveAsPath = null; int byteArraySize = 104857600;
                try {
                    byteArraySize = Integer.parseInt(byteArraySizeField.getText().toString());
                    addr = remoteAddrField.getText().toString();
                    port = Integer.parseInt(remotePortField.getText().toString());
                    saveAsPath = saveAsField.getText().toString();
                } catch (Exception x) {
                    logField.append("\n[CRIITICAL] Error: " + x + " at \n" + x.getMessage() + "\n\nExiting!\n\n"); return;
                }
                if (addr == null || saveAsPath == null) {
                    logField.append("[CRITICAL] Error: remote port or save path not set."); return;
                }
                if (true /*TODO: file check, etc.*/) {
                    Toast.makeText(getApplicationContext(), "receiving...", Toast.LENGTH_LONG).show();
                    logField.append("\nreceiving..");
                    try {
                        final pttPClient x = new pttPClient(logField); final String xaddr = addr; final int xport = port; final String xpath = saveAsPath; final int xbaz = byteArraySize;
                        new Thread() {
                            public void run() {
                                try {
                                    x.receiveFile(xaddr, xport, xpath, xbaz);
                                } catch (Throwable x) {
                                    x.printStackTrace();
                                    clRunning = 0;
                                    logField.append("[CRITICAL] Error in pttPClient: " + x + " at \n" + x.getMessage());
                                }
                            }
                        }.start();
                        logField.append("\n[DEBUG] Exited pttPClient ");
                    } catch (Throwable x) {
                        x.printStackTrace();
                        logField.append("\n[CRITICAL] Exception in pttPClient: " + x + " at \n" + x.getMessage());
                    }
                }
            }
        });
    }
    final private int REQUEST_CODE_ASK_PERMISSIONS_W = 123;
    final private int REQUEST_CODE_ASK_PERMISSIONS_R = 124;

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(hoofdvenster.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS_W);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(hoofdvenster.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS_R);
        }
    }

    /* TODO
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS_W:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    logField.append(">> Write perms granted");
                } else {
                    logField.append(">> Write perms denied");
                }
                break;
            case REQUEST_CODE_ASK_PERMISSIONS_R:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    logField.append(">> Read perms granted");
                } else {
                    logField.append(">> Read perms denied");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/
}
