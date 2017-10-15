package com.example.hp.mapapplication;

/**
 * Created by hp on 13-10-2017.
 */

public class State {

    private static State instance = null ;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;

    protected State()
    {
    }

    protected static State getInstance()
    {
        if(instance ==null)
        {
            instance = new State();

        }
        return instance;
    }

}
