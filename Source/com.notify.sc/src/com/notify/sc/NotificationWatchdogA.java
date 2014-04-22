package com.notify.sc;


import java.util.List;

import com.notify.sc.R;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;
 
public class NotificationWatchdogA extends AccessibilityService {
    SharedPreferences NotifySCPrefs;
    String regApps;
    notificationComSC receivernotificationComSC;
    String[] separatedApps;

    @Override 
	public void onAccessibilityEvent(AccessibilityEvent evt) 
	{   
		separatedApps = regApps.split(",");
		if (!regApps.contains(evt.getPackageName().toString()) && !evt.getPackageName().toString().contains("com.notify.sc"))
		{
			if (!getEventText(evt.getText()).equals("[]") && !getEventText(evt.getText()).equals(""))
			{
				String TextAcitveNot;
			    TextAcitveNot = "" + getEventText(evt.getText());
			    TextAcitveNot = TextAcitveNot.replace('[',' ');
			    TextAcitveNot = TextAcitveNot.replace(']',' ');
			    final int size = separatedApps.length;
			    for (int i = 0; i < size; i++)
			    {
			        String packageIncome = separatedApps[i];
			        sendNewNotification(evt.getPackageName().toString(), TextAcitveNot, -1337, -1337, packageIncome, null);
			    }
			}
		}
	}

    private String getEventText(List<CharSequence> msg) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : msg) {
            sb.append(s);
        }
        return sb.toString();
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
			        if (VariousFunctions.appInstalledOrNot(unregApp, getBaseContext()) && regApps.contains(unregApp))
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
    	
    	if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) 
		{
    	
	    	Notification n  = new Notification.Builder(this)
	    	        .setContentTitle("NotifySC Information")
	    	        .setContentText(dialogText)
	    	        .setSmallIcon(statusBarIcon)
	    	        .setContentIntent(pIntent)
	    	        .setAutoCancel(true).build(); 
	    	    
	    	NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    	notificationManager.notify(48202, n); 
		}
    	else
    	{

    		NotificationManager notificationManager =
    			    (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    	    Notification n = new Notification(statusBarIcon, "NotifySC Information",System.currentTimeMillis());
			Context context = getApplicationContext();
			n.setLatestEventInfo(context,
					   "NotifySC Information",
					   dialogText,
					   pIntent);
			notificationManager.notify(48201, n);
    	}
    }

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub
		
	}

}
