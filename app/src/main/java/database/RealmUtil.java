package database;

import android.content.Context;

import database.model.UserVO;
import io.realm.Realm;
import model.UserModel;

/**
 * Created by SungHyun on 2018-02-28.
 */

public class RealmUtil {
    private Context context;

    public RealmUtil(Context context){
        this.context = context;
    }

    /**
     * User Data Insert
     * @param uid
     * @param email
     * @param name
     * @param created_at
     */
    public void InsertUserData(String uid, String loginType, String email, String name, String phoneNum, String created_at){
        Realm mRealm;
        RealmConfig realmConfig;
        realmConfig = new RealmConfig();
        mRealm = Realm.getInstance(realmConfig.UserRealmVersion(context));

        mRealm.beginTransaction();
        UserVO userVO = new UserVO();
        userVO.setNo(1);
        userVO.setLoginType(loginType);
        userVO.setUid(uid);
        userVO.setEmail(email);
        userVO.setName(name);
        userVO.setPhoneNum(phoneNum);
        userVO.setCreatedAt(created_at);

        mRealm.copyToRealmOrUpdate(userVO);
        mRealm.commitTransaction();

        RefreshUserData(uid);
    }

    private void RefreshUserData(String uid){
        Realm mRealm;
        RealmConfig realmConfig;
        realmConfig = new RealmConfig();
        mRealm = Realm.getInstance(realmConfig.UserRealmVersion(context));

        UserVO userVO = mRealm.where(UserVO.class).equalTo("uid",uid).findFirst();

        UserModel.getInstance().setUid(userVO.getUid());
        UserModel.getInstance().setLoginType(userVO.getLoginType());
        UserModel.getInstance().setEmail(userVO.getEmail());
        UserModel.getInstance().setName(userVO.getName());
        UserModel.getInstance().setPhoneNum(userVO.getPhoneNum());
        UserModel.getInstance().setCreatedAt(userVO.getCreatedAt());

    }

    public void DeleteUserData(){
        RealmConfig realmConfig = new RealmConfig();
        Realm mRealm = Realm.getInstance(realmConfig.UserRealmVersion(context));
        UserVO userVO = mRealm.where(UserVO.class).equalTo("no",1).findFirst();
        mRealm.beginTransaction();
        userVO.deleteFromRealm();
        mRealm.commitTransaction();
    }
}
