package au.mccann.oztaxreturn.database;


import au.mccann.oztaxreturn.utils.LogUtils;
import io.realm.Realm;

/**
 * Created by LongBui.
 */
public class UserManager {

    private static final String TAG = UserManager.class.getName();

//    public static boolean checkLogin() {
//        Realm realm = Realm.getDefaultInstance();
//        // get last update
//        UserEntity userEntity = realm.where(UserEntity.class).equalTo("isMyUser", true).findFirst();
//
//        return !(userEntity == null || userEntity.getFullName().equals("") || userEntity.getFullName() == null);
//    }

    public static UserEntity getMyUser() {
        LogUtils.d(TAG, "getMyUser start ");
        Realm realm = Realm.getDefaultInstance();
        // get last update
        UserEntity userEntity = realm.where(UserEntity.class).equalTo("isMyUser", true).findFirst();
        if (userEntity != null) LogUtils.d(TAG, "getMyUser : " + userEntity.toString());
        return userEntity;
    }

    public static UserEntity getUserById(int id) {
        LogUtils.d(TAG, "getUser start ");
        Realm realm = Realm.getDefaultInstance();
        // get last update
        UserEntity userEntity = realm.where(UserEntity.class).equalTo("id", id).findFirst();
        if (userEntity != null) LogUtils.d(TAG, "getMyUser : " + userEntity.toString());
        return userEntity;
    }

//    public static String getUserToken() {
//        String result = "";
//        LogUtils.d(TAG, "getMyUser start ");
//        Realm realm = Realm.getDefaultInstance();
//        // get last update
//        UserEntity userEntity;
//        if (realm.where(UserEntity.class) != null) {
//            userEntity = realm.where(UserEntity.class).equalTo("isMyUser", true).findFirst();
//            if (userEntity != null && userEntity.isMyUser()) {
//                result = userEntity.getAccessToken();
//            }
//        }
//        return result;
//
//    }

    public static void insertUser(UserEntity userEntity) {
        LogUtils.d(TAG, "insertUser : " + userEntity.toString());
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(userEntity);
        realm.commitTransaction();
    }


}