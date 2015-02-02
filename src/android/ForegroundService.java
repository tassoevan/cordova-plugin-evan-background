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

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

public class ForegroundService extends Service {

    private static final int NOTIFICATION_ID = -574543954;

    private final Timer scheduler = new Timer();

    private TimerTask keepAliveTask;

    @Override
    public IBinder onBind (Intent intent) {
        return null;
    }

    @Override
    public void onCreate () {
        super.onCreate();
        
        final Handler handler = new Handler();

        startForeground(NOTIFICATION_ID, makeNotification());

        keepAliveTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(BackgroundPlugin.class.getName(), "" + new Date().getTime());
                    }
                });
            }
        };

        scheduler.schedule(keepAliveTask, 0, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        keepAliveTask.cancel();
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private Notification makeNotification() {
        JSONObject settings = BackgroundPlugin.settings;
        Context context     = getApplicationContext();
        String pkgName      = context.getPackageName();
        Intent intent       = context.getPackageManager()
                .getLaunchIntentForPackage(pkgName);

        Notification.Builder notification = new Notification.Builder(context)
            .setContentTitle(settings.optString("title", ""))
            .setContentText(settings.optString("text", ""))
            .setTicker(settings.optString("ticker", ""))
            .setOngoing(true)
            .setSmallIcon(getIconResId());

        if (intent != null && settings.optBoolean("resume")) {
            PendingIntent contentIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            notification.setContentIntent(contentIntent);
        }

        if (Build.VERSION.SDK_INT < 16) {
            return notification.getNotification();
        } else {
            return notification.build();
        }
    }

    private int getIconResId () {
        Context context = getApplicationContext();
        Resources res   = context.getResources();
        String pkgName  = context.getPackageName();

        return res.getIdentifier("icon", "drawable", pkgName);
    }
}
