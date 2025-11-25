package com.michael.ebeano.models;

import com.google.firebase.Timestamp;

public class UserDoc {
    public String id;
    public String email;
    public String passwordHash;
    public String firstName;
    public String lastName;
    public String phone;
    public boolean emailVerified;
    public Timestamp createdAt;
    public Timestamp updatedAt;
    public UserDoc() {}
}
