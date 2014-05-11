// IMPORTANT: Here you must define your packagename.
// package com.my.package.name;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

public class NotifySC {
	
	public static void SCNotificationCom(String extra, Context context, String todo)
	{
		//This is the command-send function for NotifySC (For regist/unregist)
		Intent intentRegNewApp = new Intent();
		intentRegNewApp.setAction("notificationComSC");
		intentRegNewApp.putExtra(todo, extra);
		context.sendBroadcast(intentRegNewApp);
	}
	
	
	public static void SCNotificationComDelNotification(String extraNotificationPackagename, String extraCurrentRegApp, Context context)
	{
		//This is the broadcast to remove notifications from Statusbar
		Intent intentRegNewApp = new Intent();
		intentRegNewApp.setAction("notificationComSC");
		Bundle delExtras = new Bundle();
		delExtras.putString("delnotificationfromSBpackage",extraNotificationPackagename);
		delExtras.putString("delnotificationAppsender",extraCurrentRegApp);
		intentRegNewApp.putExtras(delExtras);
		context.sendBroadcast(intentRegNewApp);
	}
	
	public static void SCDelNotification(Context context, String notificationPackageName)
	{
		//This will delete a specific notification from Statusbar
		SCNotificationComDelNotification(notificationPackageName, context.getPackageName(), context);
	}
	
	public static void SCRegApp(Context context)
	{
		//This will regist your app in NotifySC
		SCNotificationCom(context.getPackageName(),context,"regappnameSC");
	}
	
	public static void SCUnRegApp(Context context)
	{
		//This will devoke your NotifySC registration
		SCNotificationCom(context.getPackageName(),context, "unregappnameSC");
	}
	
	public static boolean SCRegStatus(Context context)
	{
		//Check if your app is registered in NotifySC
		boolean regAppStatus = true;
		File file = new File(Environment.getExternalStorageDirectory()+"/notifySC/", "appsinstalled.txt" );
		File directory = new File(Environment.getExternalStorageDirectory()+"/notifySC/");

		
		if(!directory.isDirectory()) 
		{
			regAppStatus=false;
		}
		
		if (!file.exists()) 
		{
			regAppStatus=false;
		}
		
		
		int length = (int) file.length();
		byte[] bytes = new byte[length];
		FileInputStream in = null;
		try 
		{
			in = new FileInputStream(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try 
		{
		    in.read(bytes);
		} 
		catch (Exception e)
		{
			
		}
		finally 
		{
		    try 
		    {
				in.close();
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		}

		
		String contents = new String(bytes);   
		if (!contents.contains(context.getPackageName()))
		{
			regAppStatus=false;
		}
		
		
		return regAppStatus;
	}

    public static boolean SCInstallStatus(Context context) 
    {
    	//This will check if NotifySC is installed
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try 
        {
            pm.getPackageInfo("com.notify.sc", PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) 
        { 
            app_installed = false;
        }
        return app_installed ;
    }
    
    public static boolean SCNotificationAccessStatus(Context context)
    {
    	//This will check if NotifySC Service is "running" (Notification Listener Service)
    	ContentResolver contentResolver = context.getContentResolver();
    	String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
    	String packageName = "com.notify.sc";
    	boolean notificationAccess = false;
    	
    	if ((enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName)) && isAccessibilityEnabled(context)==false)
    	{
    		notificationAccess=false;
    	}
    	else
    	{
    		notificationAccess=true;
    	}
    	return notificationAccess;
    }
    
    public static boolean isAccessibilityEnabled(Context context) 
    {
    	//Checks for older devices the access to AccessibilityService instead of NotificationListenerService
    	String packageName = "com.notify.sc/.NotificationWatchdogA";
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> runningServices = am.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo service : runningServices) 
        {
            if (packageName.equals(service.getId())) 
            {
                return true;
            }
        }

        return false;
    }
	
    public static int SCChecker(Context context)
    {
    	//This will give back an int with the current status 
    	int status = 0;
    	if (SCInstallStatus(context) == false)
    	{
    		status = 0;
    	}
    	else if (SCNotificationAccessStatus(context) == false)
    	{
    		status = 1;
    	}
    	else if (SCRegStatus(context) == false)
    	{
    		status = 2;
    	}
    	else
    	{
    		status = 3; 
    	}
    	
		return status;
    }
    
	public static void SCShowDialog(final Context context)
	{
		//This shows a dialog with a download hint
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		        	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.notify.sc"));
		        	context.startActivity(browserIntent);
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Download NotifySC");
		builder.setMessage("This app uses NotifySC. It is a service to receive notifications easily while protecting your privacy. \n\nDownload it now to receive notifications.")
			.setPositiveButton("Download", dialogClickListener)
		    .setNegativeButton("Cancel", dialogClickListener).show();
	}

}
