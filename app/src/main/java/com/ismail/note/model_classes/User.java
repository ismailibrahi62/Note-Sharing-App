package com.ismail.note.model_classes;

public  class User {
    public String name;
    public String email;
    public String profileUrl;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String profileUrl) {
        this.name = name;
        this.email = email;
        this.profileUrl = profileUrl;
    }
}
