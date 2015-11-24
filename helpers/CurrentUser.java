package fxchat.helpers;

import fxchat.models.User;

/**
 * Created by rickwhalley on 24/11/2015.
 */
public class CurrentUser {

    public final static CurrentUser instance = new CurrentUser();
    User user;


    private CurrentUser(){
        user = null;
    }

    public static CurrentUser getInstance() {
        return instance;
    }

    public void setCurrentUser(User user){
        this.user = user;
    }

    public User getUser(){
        return this.user;
    }

}
