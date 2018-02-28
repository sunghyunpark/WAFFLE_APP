package model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SungHyun on 2018-02-24.
 */

public class UserModel {
    private static volatile UserModel user = null;

    private String loginType;    //Login Type : Email / Facebook / Kakao
    private String email;
    private String uid;    //uid From Firebase
    private String name;
    @SerializedName("phone_num")
    private String phoneNum;
    @SerializedName("created_at")
    private String createdAt;

    public static  UserModel getInstance(){
        if(user == null)
            synchronized (UserModel.class){
                if(user==null){
                    user = new UserModel();
                }
            }

        return user;
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
