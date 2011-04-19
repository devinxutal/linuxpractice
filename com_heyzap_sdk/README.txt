Heyzap SDK
==========

Thank you for using the HeyZap SDK! The current implementation is very simple, and provides a quick and easy way to integrate HeyZap checkins into your game, driving viral growth and promoting your game on our social graph.

Android Version Suport
======================

SDK will not work on Android 1.5 devices. As of this writing that was less than 3.5% of the market. You will have to change your minSdkVersion to "4".

Installing on Eclipse
======================
The HeyZapSDK is installed like any other Android library project, and rolled into your game at compile time. To install it, download the library and extract it into a directory separate from your project's directory, usually a sibling of your project's directory.

Add a dependency on the Heyzap SDK library on your application:

1. Add a new Android project in Eclipse with the HeyzapSDK. File -> New -> Project -> Android Project
2. On the next screen select Create project from existing source and Browse to where you extracted the HeyzapSDK zip file. Set the project name to HeyzapSDK and finish.
3. Select File -> Properties. Open the Android section within the Properties dialog.
4. In the bottom Library section, click Add... and select the Heyzap SDK project.
5. Any issues? Check [Android documentation](http://developer.android.com/guide/developing/eclipse-adt.html#libraryProject)

Ensure that your application has network access (android.permission.INTERNET) in the Android manifest:

    <uses-permission android:name="android.permission.INTERNET"></uses-permission>

Installing from the Command Line
=================================
To install the SDK from the command line, extract the library to a directory next to your existing project's directory. From the terminal, enter your project's directory, and enter the following command:

    android update lib-project --target <ANDROID_VERSION> --path . --library ../HeyzapSDK
    
Where ANDROID_VERSION is the integer ID of the target platform you are building your game for. You can determine the id of all available target platforms by running this command:

    android list
    
Once you know the ids of the android platforms you have created, just select the one you are building your game towards for use with the update lib-project command.

Implementing a Checkin Button
=============================
The simplest way to implement the Heyzap SDK is to place a Heyzap button somewhere in your application. To do this, add the following line to any layout file:
    <com.heyzap.sdk.HeyzapButton
      android:id="@+id/hzbutton"
      android:src="@drawable/heyzap_button"
      android:layout_width="wrap_content"
      android:layout_height="wrap_content" 
      android:layout_margin="30dp"
      />

The button will automatically check in to Heyzap using your application's package name when clicked.

Manually Implementing Checkin
=============================
To check in using your own self-defined action, first choose where you want checkins to happen. You will need to import the HeyzapLib class. Add the following to the top of the file where you would like to activate the checkin:

    import com.heyzap.sdk.HeyzapLib;

Then add the following line when you would like a user to check in:

    HeyzapLib.checkin(this);

The checkin function takes one argument, which must be a Context object. It does not modify the context object, so in most cases you will pass in either "this" or "this.getContext()" or some variant thereof.

We've also included a smaller, circular button you can use for making your own checkin button. This may be friendlier for smaller interfaces. It's located at:

    HeyzapSDK/res/drawable-hdpi/heyzap_circle.png

In our example game, we set up a button to call the checkin function. So we added the following to the layout XML file we were using:

    <Button 
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      android:onClick="doCheckin"
      android:text = "Heyzap Check-in!"
      />

And then added the following function to our game's view:

    public void doCheckin(View v){
        HeyzapLib.checkin(this);
    }

Please email [Immad](mailto:immad@heyzap.com) if you have any questions.

Pushing Levels or Scores from your Game
=============================
You can additionally allow users to check-in with their score or level achievements. This normally goes at the end of a level and is a cool way to let them tell their scores/levels to their friends.

The method to do this is the same as a Manual Checkin implementation above except you pass in an extra parameter to the checkin function. Like this:

    HeyzapLib.checkin(this, "I just completed level 10!");
