package com.notify.sc;


import com.notify.sc.R;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
@SuppressLint("NewApi")
public class NotificationWatchdog extends NotificationListenerService {
    SharedPreferences NotifySCPrefs;
    String regApps;
    notificationComSC receivernotificationComSC;
    String[] separatedApps;

	@Override
	public void onNotificationPosted(StatusBarNotification sbn) 
	{
		regApps = NotifySCPrefs.getString("regApps", "");
		separatedApps = regApps.split(",");
		if (!regApps.contains(sbn.getPackageName().toString()) && !sbn.getPackageName().toString().contains("com.notify.sc"))
		{
			if (!sbn.getNotification().tickerText.equals("[]") && !sbn.getNotification().tickerText.equals(""))
			{
				
				String TextAcitveNot;
			    TextAcitveNot = "" + sbn.getNotification().tickerText.toString();
			    TextAcitveNot = TextAcitveNot.replace('[',' ');
			    TextAcitveNot = TextAcitveNot.replace(']',' ');
			    final int size = separatedApps.length;
			    for (int i = 0; i < size; i++)
			    {
			        String packageIncome = separatedApps[i];
			        sendNewNotification(sbn.getPackageName().toString(), TextAcitveNot, sbn.getId(), sbn.getNotification().icon, packageIncome, sbn.getNotification().largeIcon);
			    }
			}
		}
	}
	
	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		regApps = NotifySCPrefs.getString("regApps", "");
		if (!regApps.contains(sbn.getPackageName().toString()) && !sbn.getPackageName().toString().contains("com.notify.sc"))
		{
			if (!sbn.getNotification().tickerText.equals("[]") && !sbn.getNotification().tickerText.equals(""))
			{
				String TextAcitveNot;
			    TextAcitveNot = "" + sbn.getNotification().tickerText.toString();
			    TextAcitveNot = TextAcitveNot.replace('[',' ');
			    TextAcitveNot = TextAcitveNot.replace(']',' ');
			    final int size = regApps.length();
			    for (int i = 0; i < size; i++)
			    {
			        String packageIncome = separatedApps[i];
			        sendRemovedNotification(sbn.getPackageName().toString(), TextAcitveNot, sbn.getId(), sbn.getNotification().icon, packageIncome, sbn.getNotification().largeIcon);
			    }
			    
			}
		}
	}
	
	public void sendNewNotification(String packageName, String notificationText, int notificationID , int appIcon, String packageIncome, Bitmap largeIcon)
	{
		Intent intentSendNotification = new Intent();
		intentSendNotification.setPackage(packageIncome);
		intentSendNotification.setAction("notificationUpdateSC");
		intentSendNotification.putExtra("notificationUpdateType", "added");
		intentSendNotification.putExtra("notificationPackageNameSC", packageName);
		intentSendNotification.putExtra("notificationAppIconSC", appIcon);
		intentSendNotification.putExtra("notificationTextSC", notificationText);
		intentSendNotification.putExtra("notificationIDSC", notificationID);
		intentSendNotification.putExtra("notificationAppIconLargeSC", largeIcon);
		intentSendNotification.putExtra("notificationAppNameSC", VariousFunctions.getAppName(packageName, getBaseContext()));
		sendBroadcast(intentSendNotification);
	}
	
	public void sendRemovedNotification(String packageName, String notificationText, int notificationID, int appIcon, String packageIncome, Bitmap largeIcon)
	{
		Intent intentSendNotification = new Intent();
		intentSendNotification.setPackage(packageIncome);
		intentSendNotification.setAction("notificationUpdateSC");
		intentSendNotification.putExtra("notificationUpdateType", "removed");
		intentSendNotification.putExtra("notificationPackageNameSC", packageName);
		intentSendNotification.putExtra("notificationAppIconSC", appIcon);
		intentSendNotification.putExtra("notificationTextSC", notificationText);
		intentSendNotification.putExtra("notificationIDSC", notificationID);
		intentSendNotification.putExtra("notificationAppIconLargeSC", largeIcon);
		intentSendNotification.putExtra("notificationAppNameSC", VariousFunctions.getAppName(packageName, getBaseContext()));
		sendBroadcast(intentSendNotification);
	}
	
    public void onCreate() 
    {
        super.onCreate();
        NotifySCPrefs = getSharedPreferences("NotifySCPrefs", 0);
        regApps = NotifySCPrefs.getString("regApps", "");
        VariousFunctions.setupDirectory();
        IntentFilter notificationComSCfilter = new IntentFilter("notificationComSC");

        receivernotificationComSC = new notificationComSC();
        try
        {
        	registerReceiver(receivernotificationComSC, notificationComSCfilter);
        }
        catch (Exception e)
        {
        	
        }
    }
    
    public class notificationComSC extends BroadcastReceiver 
    {
    	@Override
    	public void onReceive(Context context, Intent intent) 
    	{
            regApps = NotifySCPrefs.getString("regApps", "");
    		try
    		{
		        String newApp = intent.getStringExtra("regappnameSC");
		        if (!newApp.equals(""))
		        {
			        if (VariousFunctions.appInstalledOrNot(newApp, getBaseContext()) && !regApps.contains(newApp))
			        {
			        	appRegNotification(newApp,1);
			        	String allAppsPlusNew;
			        	if (regApps==null)
			        	{
				        	allAppsPlusNew = newApp;
			        	}
			        	else
			        	{
				        	allAppsPlusNew = newApp + "," + regApps;
			        	}
			        	VariousFunctions.createTextFile(getBaseContext(), allAppsPlusNew);
			        	VariousFunctions.savestring("regApps", allAppsPlusNew, getBaseContext());
			        }
		        }
    		}
    		catch (Exception e)
    		{
    			
    		}
    		
    		try
    		{
		        String unregApp = intent.getStringExtra("unregappnameSC");
		        if (!unregApp.equals(""))
		        {
			        if (regApps.contains(unregApp))
			        {
			        	appRegNotification(unregApp,0);
			        	regApps = regApps.replace(unregApp + ",","");
			        	VariousFunctions.createTextFile(getBaseContext(), regApps);
			        	VariousFunctions.savestring("regApps", regApps, getBaseContext());
	
			        	try
			        	{
			        		StatusActivity.MainFragment.reload();
			        	}
			        	catch (Exception e)
			        	{
			        		
			        	}
			        }
		        }
    		}
    		catch (Exception e)
    		{
    			
    		}
    		
    		try
    		{
		        String delNotificationPackage = intent.getStringExtra("delnotificationfromSBpackage");
		        String delNotificationAppsender = intent.getStringExtra("delnotificationAppsender");
		        if (!delNotificationPackage.equals(""))
		        {
			        if (VariousFunctions.appInstalledOrNot(delNotificationAppsender, getBaseContext()) && regApps.contains(delNotificationAppsender))
			        {
	        			Log.w("NotifySC","Receive delNotification Broadcast -- pkg: " + delNotificationPackage + "-- sender: " + delNotificationAppsender);
			        	for (StatusBarNotification nfication : NotificationWatchdog.this.getActiveNotifications())
			        	{
			        		if (delNotificationPackage.equals(nfication.getPackageName()))
			        		{
			        			NotificationWatchdog.this.cancelNotification(nfication.getPackageName(),nfication.getTag(),nfication.getId());
			        			Log.w("NotifySC","Notification found, del it!");
			        		}
			        		
			        	}
			        }
		        }
    		}
    		catch (Exception e)
    		{
    			
    		}
    	}
    }
    public void onDestroy()
    {
    	super.onDestroy();
    	try
    	{
    		unregisterReceiver(receivernotificationComSC);
    	} 
    	catch (Exception e)
    	{
    		
    	}
    }
    
    public void appRegNotification(String packagename, int type)
    {
    	Intent intent = new Intent(this, StatusActivity.class);
    	PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
    	int statusBarIcon = 0;
    	
    	String dialogText = "";
    	if (type==1)
    	{
    		statusBarIcon = R.drawable.ic_statusbar;
    		dialogText = getResources().getString(R.string.appreg_notification_1) + " " +  VariousFunctions.getAppName(packagename, getBaseContext()) + " " +  getResources().getString(R.string.appreg_notification_2);
    	}
    	else
    	{
    		statusBarIcon = R.drawable.ic_statusbar_d;
    		dialogText = getResources().getString(R.string.appunreg_notification_1) + " " +  VariousFunctions.getAppName(packagename, getBaseContext()) + " " +  getResources().getString(R.string.appunreg_notification_2);
    	}
    	
    	Notification n  = new Notification.Builder(this)
    	        .setContentTitle("NotifySC Information")
    	        .setContentText(dialogText)
    	        .setSmallIcon(statusBarIcon)
    	        .setContentIntent(pIntent)
    	        .setAutoCancel(true).build();
    	    
    	NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	notificationManager.notify(48203, n); 
    }

}
