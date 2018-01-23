package nl.hypothermic.android.petatransfer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class hoofdvenster extends AppCompatActivity {

    protected Button sendButton;
    protected TextView logField;
    protected EditText sendPortField;
    protected EditText sendFilePathField;
    protected static int srvRunning = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoofdvenster);
        sendButton = (Button) findViewById(R.id.sendButton);
        logField = (TextView) findViewById(R.id.logField);
        sendPortField = (EditText) findViewById(R.id.sendPortField);
        sendFilePathField = (EditText) findViewById(R.id.sendFilePathField);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int port = 9070; String path = null;
                try {
                    port = Integer.parseInt(sendPortField.getText().toString());
                    path = sendFilePathField.getText().toString();
                } catch (Exception x) {
                    logField.append("\n[CRIITICAL] Error: " + x + " at \n" + x.getMessage());
                }
                if (true) {
                    Toast.makeText(getApplicationContext(), "sending...", Toast.LENGTH_LONG).show();
                    log("sending..");
                    try {
                        pttPSender.send(port, path);
                    } catch (IOException x) {
                        logField.append("\n[CRITICAL] IOException in pttPSender: " + x + " at \n" + x.getMessage());
                    }
                }
            }
        });
    }

    public static void log(String xs) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        hoofdvenster xf = new hoofdvenster();
        TextView lgF = xf.xsLF();
        lgF.setMovementMethod(new ScrollingMovementMethod());
        lgF.append("\n" + sdf.format(cal.getTime()) + " > " + xs);
    }

    public TextView xsLF() {
        TextView x = findViewById(R.id.logField);
        return x;
    }
}
