package huzefagadi.com.icu;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CommonUtility {
	


	Context mContext;
	public static final String ICU_TOPIC = "icu";
	SharedPreferences sharedPreferences;
	public static final String USERNAME="username";
	public static final String LOCATION="location";
	public CommonUtility(Context context)
	{
		try {
			mContext = context;
			sharedPreferences = mContext.getSharedPreferences("ICU",Context.MODE_PRIVATE);

		} catch (Exception e) {
			// TODO Auto-generated catch block

		}

	}

	public void writeToPreference(String key,String value)
	{
		sharedPreferences.edit().putString(key,value).commit();
	}

	public String getValueFromPreferences(String key)
	{
		return sharedPreferences.getString(key,null);
	}
	public static String convertStackTraceToString(Exception exception) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			exception.printStackTrace(pw);
			return sw.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return e.getLocalizedMessage();
		}
	}

	public boolean checkInternetConnectivity() {
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getApplicationContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivityManager != null) {
				NetworkInfo networkInfo = connectivityManager
						.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				NetworkInfo mobileNetworkInfo = connectivityManager
						.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				if (networkInfo != null || mobileNetworkInfo != null) {
					if (networkInfo.isConnected()) {
						return true;
					} else if (mobileNetworkInfo.isConnected()) {
						return true;
					}

				}
			}
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		}
	}
}
