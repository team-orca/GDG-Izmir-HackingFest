package com.example.orca.howbeautyiam;

public class Photos {


        private String photoPoint;
        private String photoUrl;
        private String username;
        private String date;


    public Photos(String url, String point, String username, String date) {

            this.photoUrl = url;
            this.photoPoint = point;
            this.username =username;
            this.date =date;

        }
    public String getPhotoUrl()
    {
        return photoUrl;
    }
    public String getPhotoPoint(){return photoPoint;}

    public String getUsername() {    return username;    }

    public String getDate() {        return date;    }
}
