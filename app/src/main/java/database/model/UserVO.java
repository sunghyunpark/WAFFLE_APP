package database.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by SungHyun on 2018-02-28.
 */

public class UserVO extends RealmObject {
    @PrimaryKey
    private int no;
    private String loginType;    //Login Type : Email / Facebook / Kakao
    private String email;
    private String uid;    //uid From Firebase
    private String name;
    private String phoneNum;
    private String createdAt;

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
