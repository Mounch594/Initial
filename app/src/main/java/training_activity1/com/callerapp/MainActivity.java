package training_activity1.com.callerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get intent object sent from the IncomingCallReceiver
        Intent intent=getIntent();
        Bundle b=intent.getExtras();
        TextView tv=(TextView)findViewById(R.id.txtmessage);
        if(b!=null){
            // Display rejected number in the TextView
            tv.setText(b.getString("number"));
        }

    }

}
