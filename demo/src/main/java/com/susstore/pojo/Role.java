package com.susstore.pojo;

public class Role {
    private Integer roleId;
    private String name;

    public Role() {
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role(Integer roleId, String name) {
        this.roleId = roleId;
        this.name = name;
    }
}
