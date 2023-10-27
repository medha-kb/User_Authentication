package com.intuit.api.dto;

import java.util.Set;

import com.intuit.model.entity.Role;

public class ViewUserResponse {

    Long id;
    String username;
    String email;
    Set<Role> Roles;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Role> getRoles() {
        return this.Roles;
    }

    public void setRoles(Set<Role> Roles) {
        this.Roles = Roles;
    }

    public ViewUserResponse(Long id, String username, String email, Set<Role> Roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.Roles = Roles;
    }

}
