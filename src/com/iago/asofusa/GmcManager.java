package com.iago.asofusa;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.iago.asofusa.utils.Utils;

/**
 * GmcManager. GMC registration/unregistration
 * @author iago
 *
 */
public class GmcManager {
	
	public static void registerGCM(Context context) {
		// Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(context);
        // Make sure the manifest was properly set - comment out ctx line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(context);
        
		String regId = GCMRegistrar.getRegistrationId(context);
        if (regId.equals("")) {
        	GCMRegistrar.register(context, "537359134324"); //Sender ID
        } else {
        	GCMRegistrar.unregister(context);
        	Log.v(Utils.tag, "Already registered. I will be unregistered");
        	GCMRegistrar.register(context, "537359134324"); //Sender ID
        }
	} 

}
