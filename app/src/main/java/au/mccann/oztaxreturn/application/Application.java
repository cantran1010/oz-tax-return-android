package au.mccann.oztaxreturn.application;

import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;

import au.mccann.oztaxreturn.database.RealmDbHelper;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;


public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        //noinspection deprecation
        Realm.init(this);
        Realm.setDefaultConfiguration(RealmDbHelper.getRealmConfig(getApplicationContext()));
//
//        TypefaceContainer.init(getApplicationContext());
//
        //fix bug capture image on s8
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

//        if (!BuildConfig.DEBUG) {
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);
//            if (UserManager.checkLogin())
//                Crashlytics.setInt("user_id", UserManager.getMyUser().getId());
//        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
