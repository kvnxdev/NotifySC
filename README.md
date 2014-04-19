Introduction
========
Hey developer, hey user.
With NotifySC it is much easier to work with notifications in android. I attached the sourcecode, class and an example to check out. I will explain below what it is, how to use it and why you should use it.



What Is NotifySC?
========
NotifySC is the best solution for users and developer to work with notifications in android. The user can easily control the notification access with NotifySC and can be sure that <b>no private data</b> will get in bad hands. <b>The developer just need 5 minutes</b> to get the first notification on his/her activity with only one java class. It is so easy to setup and to use.


Download & Install
========
1. Download the NotifySC.java in the folder "Class".
2. Drag and drop the class in your package in Eclipse/Android Studio where your main classes are (E.g. com.test.app)
3. You are ready! Easy, isn't it?


Context Information
========
<b>Activity:</b> "<i>getBaseContext()</i>", "<i>this</i>" or which context you ever want to use.<br>
<b>Fragment:</b> You should define "<i>Context myFragmentContext = container.getContext();</i>" in onCreateView


Functions
========
```javascript
//Checks everything (0=nothing/1=installed/2=installed+running/3=installed+running+active):
NotifySC.SCChecker(getBaseContext());
```

```javascript
//Check if NotifySC is installed (true/false):
NotifySC.SCInstallStatus(getBaseContext());
```

```javascript
//Check if NotifySC Service is running (true/false):
NotifySC.SCNotificationAccessStatus(getBaseContext());
```

```javascript
//Check if your app is registed in NotifySC (true/false):
NotifySC.SCRegStatus(getBaseContext());
```

```javascript
//***Register your app:
NotifySC.SCRegApp(getBaseContext());
```

```javascript
//***Unregist your app:
NotifySC.SCUnRegApp(getBaseContext());
```

```javascript
//Shows a dialog that the user need to download NotifySC:
NotifySC.SCShowDialog(getBaseContext());
```

&#42;&#42;&#42; = These functions are only working when the NotifySC service is running!


Example (Settings Activity)
========
- <b>STEP 1 // SETUP FUNCTION:</b><br>
Create the setup function. This is just an example. You can use it in so much ways.

```javascript
public void setupNotifySC()
{
  int notifySCStatus= NotifySC.SCChecker(getBaseContext());
  switch(position)
  {
    case 0:
        //The user need to install NotifySC first (Display a dialog?)
        break;
    case 1:
        //The user need to start the NotifySC Service (Open NotifySC?)
        break;
    case 2:
        //Register your app
        break;
    case 3:
        //Everything is fine
        break;
    default:
        //The user need to install NotifySC first (Display a dialog?)
  }
}
```


- <b>STEP 2 // EDIT ONCREATE</b><br>
Add the setup function f.e. to your onCreate.

```javascript
public void onCreate(Bundle savedInstanceState) 
{
  super.onCreate(savedInstanceState);
  setupNotifySC();
}
```


Example (Notification Acitvity)
========
- <b>STEP 1 // DEFINE BROADCASTRECEIVER</b><br>
You need to define a broadcastreceiver to receive notifications

```javascript
SCNnotificationReceiver SCIncome;
```


- <b>STEP 2 // CREATE BROADCASTRECEIVER</b><br>
Setup and Create the broadcastreceiver to receive notifications

```javascript
//The BroadcastReceiver Setup
public void setupSCListener()
{
  SCIncome = new SCNnotificationReceiver();
  IntentFilter notificationFilter = new IntentFilter("notificationUpdateSC");
  try { registerReceiver(SCIncome, notificationFilter); } catch (Exception e) {}
}
    
//The BroadcastReceiver
public class SCNnotificationReceiver extends BroadcastReceiver 
{
  @Override
  public void onReceive(Context context, Intent intent) 
  {
    try
    {
      // Notification Type (Removed or Added notification)
      String incomeType = intent.getStringExtra("notificationUpdateType");
      
      // Notification-App information
      String incomeAppPackageName = intent.getStringExtra("notificationPackageNameSC"); 
      int incomeNotificationAppIcon = intent.getIntExtra("notificationAppIconSC",0); 
      String incomeNotificationAppName = intent.getStringExtra("notificationAppNameSC"); 
      
      // Notification Content
      String incomeNotificationText = intent.getStringExtra("notificationTextSC"); 
      int incomeNotificationID = intent.getIntExtra("notificationIDSC",0); 
      
      if (incomeType.equals("added"))
      {
        //A NEW NOTIFICATION GOT ADDED (Now you can f.e. add a item to a ListView.)
        Log.v("com.notifySC.service","New notification from App " + incomeNotificationAppName + "(" + incomeAppPackageName + ")");
        Log.v("com.notifySC.service","Notification " + incomeNotificationText);
        Log.v("com.notifySC.service","Notification ID " + incomeNotificationID);
      }
      else if (incomeType.equals("removed"))
      {
        //A NEW NOTIFICATION GOT REMOVED (F.e. remove item from your ListView)
        Log.v("com.notifySC.service","Removed notification from App " + incomeNotificationAppName + "(" + incomeAppPackageName + ")");
        Log.v("com.notifySC.service","Notification " + incomeNotificationText);
        Log.v("com.notifySC.service","Notification ID " + incomeNotificationID);
      }
    }
    catch (Exception e)
    {
    	
    }
  }
}
```


- <b>STEP 3 // CANCEL/START BROADCASTRECEIVER</b><br>
Create the broadcastreceiver to receive notifications

```javascript
//You need to decide if you want to unregister it on onDestroy/onPause
public void onDestroy()
{
  super.onDestroy();
  try { unregisterReceiver(SCIncome); } catch (Exception e) {}
}

//Also decide if you want to create it once in onCreate or if you want to resume it in onResume
public void onCreate(Bundle savedInstanceState) 
{
  super.onCreate(savedInstanceState);
  setupSCListener();
}
```
