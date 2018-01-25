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
    protected Button rqpButton;
    public String sdLoc = System.getenv("EXTERNAL_STORAGE");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*TEMP: om android.os.NetworkOnMainThreadException te voorkomen*/
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoofdvenster);
        sendButton = findViewById(R.id.sendButton);
        logField = findViewById(R.id.logField);
        sendPortField = findViewById(R.id.sendPortField);
        sendFilePathField = findViewById(R.id.sendFilePathField); sendFilePathField.setText(sdLoc);
        rqpButton = findViewById(R.id.rqPerms);

        rqpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                logField.append("\n>> Requesting R+W permissions");
                requestPermission();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logField.setMovementMethod(new ScrollingMovementMethod());
                if (srvRunning == 1) { logField.append("\n[INFO] Cannot send file, process already running!"); return; } else { srvRunning = 1; }
                int port = 9070; String path = null;
                try {
                    port = Integer.parseInt(sendPortField.getText().toString());
                    path = sendFilePathField.getText().toString();
                } catch (Exception x) {
                    logField.append("\n[CRIITICAL] Error: " + x + " at \n" + x.getMessage());
                }
                if (true) {
                    Toast.makeText(getApplicationContext(), "sending...", Toast.LENGTH_LONG).show();
                    logField.append("sending..");
                    try {
                        final pttPSender x = new pttPSender(logField); final int xport = port; final String xpath = path;
                        new Thread() {
                            public void run() {
                                System.out.println("blah");
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

    //TODO
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
    }
}
