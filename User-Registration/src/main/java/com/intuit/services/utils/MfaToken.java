package com.intuit.services.utils;

public class MfaToken {
    private String useremail;
    private String token;
    private boolean isValidated;
    // getters, setters, etc.

    public MfaToken(String useremail, String token, Boolean isValid) {
        this.isValidated = isValid;
        this.token = token;
        this.useremail = useremail;
    }

    public String getUseremail() {
        return this.useremail;
    }

    public void setUseremain(String useremail) {
        this.useremail = useremail;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isIsValidated() {
        return this.isValidated;
    }

    public boolean getIsValidated() {
        return this.isValidated;
    }

    public void setIsValidated(boolean isValidated) {
        this.isValidated = isValidated;
    }
}
