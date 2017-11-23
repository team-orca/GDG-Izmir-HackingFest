package com.example.orca.howbeautyiam;

/**
 * Created by muharrem on 07.05.2016.
 */

public class User {
    private String nickName;
    private String mailAddres;
    private String password;
    private String point;
    private String spoint;
    public User(String nickName, String mailAddres, String password ) {
        this.nickName = nickName;
        this.mailAddres = mailAddres;
        this.password = password;
        this.point = "0";

    }

    public User(String nickName, String mailAddres, String sP, String a) {
        this.nickName = nickName;
        this.mailAddres = mailAddres;
        this.password = password;
        this.point = "0";
        spoint = sP;
    }

    public String getNickName() {
        return nickName;
    }

    public String getMailAddres() {
        return mailAddres;
    }

    public String getPassword() {
        return password;
    }
    public String getPoint() {
        return point;
    }
    public String getSPoint() {
        return spoint;
    }


}

