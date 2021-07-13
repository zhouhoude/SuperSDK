//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sxsdk.collection.util.lundu;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class DeviceGaidProvider {
    DeviceGaidProvider() {
    }

    static DeviceGaidProvider.AdInfo getAdvertisingIdInfo(Context context) throws Exception {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("Cannot be called from the main thread");
        } else {
            try {
                PackageManager pm = context.getPackageManager();
                pm.getPackageInfo("com.android.vending", 0);
            } catch (Exception var11) {
                throw var11;
            }

            DeviceGaidProvider.AdvertisingConnection connection = new DeviceGaidProvider.AdvertisingConnection();
            Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
            intent.setPackage("com.google.android.gms");

            try {
                if (context.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {
                    DeviceGaidProvider.AdInfo var5;
                    try {
                        DeviceGaidProvider.AdvertisingInterface adInterface = new DeviceGaidProvider.AdvertisingInterface(connection.getBinder());
                        DeviceGaidProvider.AdInfo adInfo = new DeviceGaidProvider.AdInfo(adInterface.getId(), adInterface.isLimitAdTrackingEnabled(true));
                        var5 = adInfo;
                    } catch (Exception var12) {
                        throw new IOException("Google Play connection failed");
                    } finally {
                        context.unbindService(connection);
                    }

                    return var5;
                }
            } catch (Exception var14) {
            }

            throw new IOException("Google Play connection failed");
        }
    }

    private static final class AdvertisingInterface implements IInterface {
        private IBinder binder;

        AdvertisingInterface(IBinder pBinder) {
            this.binder = pBinder;
        }

        public IBinder asBinder() {
            return this.binder;
        }

        String getId() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();

            String id;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                this.binder.transact(1, data, reply, 0);
                reply.readException();
                id = reply.readString();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return id;
        }

        boolean isLimitAdTrackingEnabled(boolean paramBoolean) throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();

            boolean limitAdTracking;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                data.writeInt(paramBoolean ? 1 : 0);
                this.binder.transact(2, data, reply, 0);
                reply.readException();
                limitAdTracking = 0 != reply.readInt();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return limitAdTracking;
        }
    }

    private static final class AdvertisingConnection implements ServiceConnection {
        boolean retrieved;
        private final LinkedBlockingQueue<IBinder> queue;

        private AdvertisingConnection() {
            this.retrieved = false;
            this.queue = new LinkedBlockingQueue(1);
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                this.queue.put(service);
            } catch (Exception var4) {
            }

        }

        public void onServiceDisconnected(ComponentName name) {
        }

        IBinder getBinder() throws InterruptedException {
            if (this.retrieved) {
                throw new IllegalStateException();
            } else {
                this.retrieved = true;
                return (IBinder)this.queue.take();
            }
        }
    }

    public static final class AdInfo {
        private final String advertisingId;
        private final boolean limitAdTrackingEnabled;

        AdInfo(String advertisingId, boolean limitAdTrackingEnabled) {
            this.advertisingId = advertisingId;
            this.limitAdTrackingEnabled = limitAdTrackingEnabled;
        }

        String getId() {
            return this.advertisingId;
        }

        public boolean isLimitAdTrackingEnabled() {
            return this.limitAdTrackingEnabled;
        }
    }
}
