package com.giornosmp.shop.api.v1.models;

import com.google.gson.annotations.Expose;

/**
 * An online player
 */
public class Player {

    @Expose
    private String displayName = null;

    @Expose
    private String address = null;


    public Player displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * The Player's display name
     *
     * @return displayName
     **/
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Player address(String address) {
        this.address = address;
        return this;
    }

    /**
     * The address the Player is connected from (usually an IP)
     *
     * @return address
     **/
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}