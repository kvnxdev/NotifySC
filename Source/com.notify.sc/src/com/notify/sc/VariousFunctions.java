package com.notify.sc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

public class VariousFunctions {
    
	public static void openActivity(Class<?> openClass, Context context)
	{
		Intent openIntent = new Intent(context, openClass);
		context.startActivity(openIntent);
	}
    
    public static boolean notificationAccessStatus(Context context)
    {
    	ContentResolver contentResolver = context.getContentResolver();
    	String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
    	String packageName = "com.notify.sc";
    	boolean notificationAccess = false;
    	
    	if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName))
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


	public static void savestring(String name, String savethis, Context context)
	{
		SharedPreferences attentivePrefs = context.getSharedPreferences("NotifySCPrefs", 0);
	   	SharedPreferences.Editor e =  attentivePrefs.edit();
		e.putString(name, savethis);
		e.commit();  
	}

	public static void saveint(String name, int savethis, Context context)
	{
		SharedPreferences attentivePrefs = context.getSharedPreferences("NotifySCPrefs", 0);
	   	SharedPreferences.Editor e =  attentivePrefs.edit();
		e.putInt(name, savethis); // add or overwrite someValue
		e.commit(); // this saves to disk and notifies observers  
	}

    
    public static String getAppName(String packagename, Context context)
    {
    	final PackageManager pm = context.getPackageManager();
    	ApplicationInfo ai;
    	try {
    	    ai = pm.getApplicationInfo(packagename, 0);
    	} catch (final NameNotFoundException e) {
    	    ai = null;
    	}
    	String applicationName;
    	return applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
    }

    static boolean appInstalledOrNot(String uri, Context context) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed ;
    }
    
	public static void setupDirectory()
	{
		try
		{
			File directory = new File(Environment.getExternalStorageDirectory()+"/notifySC/");
			if(!directory.isDirectory()) {
				directory.mkdirs();
			}
		}
		catch (Exception e)
		{
			
		}
	}
	
	public static void createTextFile(Context context, String text)
	{
		String FILENAME = "appsinstalled.txt";
		String appText = text;
		File directory = new File(Environment.getExternalStorageDirectory()+"/notifySC/");
		if(!directory.isDirectory()) {
			directory.mkdirs();
		}
		
		BufferedWriter out = null;
	    File log = new File(Environment.getExternalStorageDirectory()+"/notifySC/", FILENAME);
	    try {
	        out = new BufferedWriter(new FileWriter(log.getAbsolutePath(), false));
	        out.write(appText);
	    } catch (Exception e) {
	        
	    }
	    try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void createDialog(final String[] apps, final Context context)
	{
		final int size = apps.length;
		final String[] items = new String[size];
		for (int i = 0; i < size; i++)
		{
			items[i] = getAppName(apps[i],context);
		}
		

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getResources().getString(R.string.dialog_title_registrated));
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	try
		    	{
			    	showRevokeDialog(context, apps[item]);
		    	}
		    	catch (Exception e)
		    	{
		    		
		    	}
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	public static void showRevokeDialog(final Context context, final String packagename)
	{
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		        	Intent intentRegNewApp = new Intent();
		    		intentRegNewApp.setAction("notificationComSC");
		    		intentRegNewApp.putExtra("unregappnameSC",packagename);
		    		context.sendBroadcast(intentRegNewApp);
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getResources().getString(R.string.dialog_title_revoke));
		builder.setMessage(context.getResources().getString(R.string.revoke_text))
			.setPositiveButton(context.getResources().getString(R.string.dialog_yes), dialogClickListener)
		    .setNegativeButton(context.getResources().getString(R.string.dialog_no), dialogClickListener).show();
	}
	
	public static void showAcceptDialog(final Context context, final Activity activity)
	{
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		            saveint("tosStatus",1, context);
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		        	activity.finish();
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("NotifySC ToS");
		builder.setMessage(Html.fromHtml(context.getResources().getString(R.string.tos_text)))
			.setPositiveButton(context.getResources().getString(R.string.dialog_accept), dialogClickListener)
		    .setNegativeButton(context.getResources().getString(R.string.dialog_cancel), dialogClickListener).show();
	}
}
