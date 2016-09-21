package huzefagadi.com.icu;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
        {
            List<String> listOfPermissions = new ArrayList<>();

            try {
                PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
                if (info.requestedPermissions != null) {

                    for (String p : info.requestedPermissions) {
                        if (ContextCompat.checkSelfPermission(this,p)!= PackageManager.PERMISSION_GRANTED)
                        {
                            listOfPermissions.add(p);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String permissionsRequired[] = Arrays.copyOf(listOfPermissions.toArray(), listOfPermissions.toArray().length, String[].class);;
            ActivityCompat.requestPermissions(this,
                    permissionsRequired,
                    100);
        }



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
