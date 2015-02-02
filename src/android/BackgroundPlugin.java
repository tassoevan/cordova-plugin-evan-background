/**
 * Copyright (c) 2013-2014 appPlant UG
 * Copyright (c) 2015 Tasso Evangelista
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package me.tassoevan.cordova;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BackgroundPlugin extends CordovaPlugin {
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            if ("configure".equals(action)) {
                return this.doConfigureAction(args, callbackContext);
            } else if ("enable".equals(action)) {
                return this.doEnableAction(args, callbackContext);
            } else if ("disable".equals(action)) {
                return this.doDisableAction(args, callbackContext);
            } else {
                this.logError("Called invalid action: " + action);
                return false;
            }
        } catch (Exception e) {
            this.logError("Exception occurred: ".concat(e.getMessage()));
            callbackContext.error("Exception occurred: ".concat(e.getMessage()));
            return false;
        }
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        inBackground = true;
        startService();
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        inBackground = false;
        stopService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService();
    }

    private boolean doConfigureAction(JSONArray args, CallbackContext callbackContext) throws JSONException {
        settings = args.getJSONObject(0);

        if (isBind) {
            stopService();
            startService();
        }

        callbackContext.success();
        return true;
    }

    private boolean doEnableAction(JSONArray args, CallbackContext callbackContext) throws JSONException {
        isDisabled = false;

        if (inBackground) {
            startService();
        }

        callbackContext.success();
        return true;
    }

    private boolean doDisableAction(JSONArray args, CallbackContext callbackContext) throws JSONException {
        stopService();
        isDisabled = true;

        callbackContext.success();
        return true;
    }

    private boolean inBackground = false;

    private boolean isDisabled = true;

    private boolean isBind = false;

    protected static JSONObject settings = new JSONObject();

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(BackgroundPlugin.class.getName(), "Service connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(BackgroundPlugin.class.getName(), "Service disrupted");
        }
    };

    private void logError(String description) {
        Log.e(BackgroundPlugin.class.getName(), description);
    }

    private void startService() {
        Activity context = cordova.getActivity();

        Intent intent = new Intent(context, ForegroundService.class);

        if (isDisabled || isBind) {
            return;
        }

        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);

        context.startService(intent);

        isBind = true;
    }

    private void stopService() {
        Activity context = cordova.getActivity();

        Intent intent = new Intent(context, ForegroundService.class);

        if (isBind) {
            context.unbindService(connection);
        }

        context.stopService(intent);

        isBind = false;
    }
}
