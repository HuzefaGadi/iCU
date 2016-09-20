package huzefagadi.com.icu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignInActvity extends AppCompatActivity {

    EditText et_username;
    Button bt_signIn;
    CommonUtility commonUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_actvity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        commonUtility =new CommonUtility(this);
        et_username = (EditText) findViewById(R.id.editText);
        bt_signIn = (Button) findViewById(R.id.signIn);
        bt_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = et_username.getText().toString();
                if(username.trim().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Username cannot be empty!!",Toast.LENGTH_LONG).show();
                }
                else
                {
                    commonUtility.writeToPreference(CommonUtility.USERNAME,username);
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}
