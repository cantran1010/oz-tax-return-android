package au.mccann.oztaxreturn.database;

import android.content.Context;
import android.util.Base64;

import java.util.Random;

import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.utils.PreferUtils;
import io.realm.RealmConfiguration;

/**
 * Created by LongBui on 4/12/17.
 */
public class RealmDbHelper {

    private static RealmConfiguration realmConfiguration;
    private static String key = null;

    public static RealmConfiguration getRealmConfig(Context context) {
        if (key == null) {
            if (PreferUtils.getKeyEncryption(context).equals(Constants.KEY_ENCRYPTION_DEFAULT)) {
                byte[] b = new byte[64];
                new Random().nextBytes(b);
                key = Base64.encodeToString(b, Base64.DEFAULT);
                PreferUtils.setKeyEncryption(context, key);
            } else {
                key = PreferUtils.getKeyEncryption(context);
            }
        }

        if (realmConfiguration == null)
            realmConfiguration = new RealmConfiguration.Builder()
                    .name(Constants.DB_NAME)
//                    .schemaVersion(5)
//                    .migration(migration)
                    .deleteRealmIfMigrationNeeded()
                    .encryptionKey(Base64.decode(key, Base64.DEFAULT))
                    .build();

        return realmConfiguration;
    }

//    private static final RealmMigration migration = new RealmMigration() {
//        @Override
//        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
////            LogUtils.d(TAG, "RealmMigration , oldVersion : " + oldVersion + " , newVersion : " + newVersion);
//            // DynamicRealm exposes an editable schema
//            RealmSchema schema = realm.getSchema();
//
//        }
//    };

}