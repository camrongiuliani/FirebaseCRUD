package com.aztechdev.CodeTest_Camron_Giuliani.classes;

import android.support.annotation.Nullable;

import java.io.Serializable;

// Camron Giuliani
// JAV2 - C201702
// .java
public class Member implements Serializable {
    private String userId;
    private final String firstName;
    private final String lastName;
    private final Long zipCode;
    private final String birthDate;
    private final String phoneNumber;

    public Member(@Nullable String userId, String firstName, String lastName, Long zipCode, String birthDate, String phoneNumber) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.zipCode = zipCode;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getZipCode() {
        return zipCode;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
