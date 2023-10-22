package com.example.Adapt200Bot;

import java.util.ArrayList;

public class User {
    String username;
    long chatID;
    int questNumber;
    ArrayList<Boolean> userAnswers;

    public User (long chatID){
        this.username = username;
        this.chatID = chatID;
        userAnswers = new ArrayList<>();
    }
}
