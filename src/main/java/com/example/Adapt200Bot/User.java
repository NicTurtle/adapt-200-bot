package com.example.Adapt200Bot;

import java.util.ArrayList;

public class User {
    private String username;
    private long chatID;
    private int questNumber;
    private ArrayList<Boolean> userAnswers;

    public User (long chatID){
        this.setUsername(getUsername());
        this.setChatID(chatID);
        setUserAnswers(new ArrayList<>());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getChatID() {
        return chatID;
    }

    public void setChatID(long chatID) {
        this.chatID = chatID;
    }

    public int getQuestNumber() {
        return questNumber;
    }

    public void setQuestNumber(int questNumber) {
        this.questNumber = questNumber;
    }

    public ArrayList<Boolean> getUserAnswers() {
        return userAnswers;
    }

    public void setUserAnswers(ArrayList<Boolean> userAnswers) {
        this.userAnswers = userAnswers;
    }
}
