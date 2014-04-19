package com.notify.sc;



import com.notify.sc.R;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends ActionBarActivity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);

		if (savedInstanceState == null) 
		{
			getSupportFragmentManager().beginTransaction().add(R.id.container, new MainFragment()).commit();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the  menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.status, menu);
		return true; 
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_devpage) 
		{ 	
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=KvnX+Dev"));
			startActivity(browserIntent);
		}
		if (id == R.id.action_devsheet) 
		{
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/kvnxdev/NotifySC/"));
			startActivity(browserIntent);
		}
		return super.onOptionsItemSelected(item); 
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class MainFragment extends Fragment {

		static Context fragmentContext;
		static View rootView;
		static RelativeLayout itemService;
		static RelativeLayout itemReg;
		static RelativeLayout notificationAccessWarning;
		static TextView textViewStatus, textViewAppCount;
		static Button settingsOpenNotificationAccess;
		static SharedPreferences NotifySCPrefs;
		static String regApps;
		static String[] separatedApps;
		static int tosStatus;
		public MainFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			rootView = inflater.inflate(R.layout.fragment_status,container, false);
			fragmentContext = container.getContext();
			setupValues();
			if (tosStatus==0)
			VariousFunctions.showAcceptDialog(fragmentContext, getActivity());
			setupLayout();
			setupOnClickListener();
			setupCheckStatus();
			VariousFunctions.setupDirectory();
			return rootView;
		}
		
		public static void setupValues()
		{

	        NotifySCPrefs = fragmentContext.getSharedPreferences("NotifySCPrefs", 0);
	        regApps = NotifySCPrefs.getString("regApps", "");
	        tosStatus = NotifySCPrefs.getInt("tosStatus", 0);
	        separatedApps = regApps.split(",");
	        
		}
		public static void setupLayout()
		{
			notificationAccessWarning = (RelativeLayout)rootView.findViewById(R.id.notificationAccessWarning);
			itemService = (RelativeLayout)rootView.findViewById(R.id.itemService); 
			textViewStatus = (TextView)rootView.findViewById(R.id.textViewStatus);
			settingsOpenNotificationAccess = (Button)rootView.findViewById(R.id.settingsOpenNotificationAccess);
			textViewAppCount = (TextView)rootView.findViewById(R.id.textViewAppCount);
			itemReg = (RelativeLayout)rootView.findViewById(R.id.itemReg); 
			VariousFunctions.createTextFile(fragmentContext, regApps);
			if (separatedApps[0].length()==0)
			{
				textViewAppCount.setText("0");
			}
			else
			{
				textViewAppCount.setText("" + (separatedApps.length));
			}
		}
		
		public void setupCheckStatus()
		{
			if (VariousFunctions.notificationAccessStatus(fragmentContext)==true || VariousFunctions.isAccessibilityEnabled(fragmentContext)==true)
			{ 
				textViewStatus.setText(getResources().getString(R.string.started));
				textViewStatus.setTextColor(Color.parseColor("#619E58"));
				notificationAccessWarning.setBackgroundColor(Color.parseColor("#9ED196"));
				settingsOpenNotificationAccess.setText(getResources().getString(R.string.stop_service));
			}
			else
			{
				textViewStatus.setText(getResources().getString(R.string.stopped));
				textViewStatus.setTextColor(Color.parseColor("#A05959"));
				notificationAccessWarning.setBackgroundColor(Color.parseColor("#D19696"));
				settingsOpenNotificationAccess.setText(getResources().getString(R.string.start_service));
			}
			
			if (VariousFunctions.notificationAccessStatus(fragmentContext)==true && VariousFunctions.isAccessibilityEnabled(fragmentContext)==true)
			{ 
				
			}
		}
		
		public void setupOnClickListener()
		{ 
			settingsOpenNotificationAccess.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) 
					{
						Intent intent=new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
						startActivity(intent);
					}
					else
					{
					    Intent i = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
						startActivity(i);
					}

				}
			});
			itemService.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) 
					{
						Intent intent=new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
						startActivity(intent);
					}
					else
					{
					    Intent i = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
						startActivity(i);
					}
				}
			});
			itemReg.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if ((separatedApps[0].length()!=0))
					VariousFunctions.createDialog(separatedApps, fragmentContext);
					else
						Toast.makeText(fragmentContext, getResources().getString(R.string.info_noregapps), Toast.LENGTH_SHORT).show();
				}
			});
			
			
		}
		
		public static void reload()
		{

			setupValues();
			setupLayout();
		}
		
		public void onResume()
		{
			super.onResume();
			Log.w("com.notify.sc","resume");
			setupCheckStatus();
		}
		
		
	}

}
