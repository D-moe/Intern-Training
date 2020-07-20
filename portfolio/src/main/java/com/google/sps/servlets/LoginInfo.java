package com.google.sps.servlets;

import java.util.Objects;

/**
 * Store data for user login.
 */
public class LoginInfo {
  private boolean loggedIn;
  private String userName;

  public LoginInfo() {
    loggedIn = false;
    userName = "";
  }

  public LoginInfo(boolean loggedIn, String userName) {
    this.loggedIn = loggedIn;
    this.userName = userName;
  }

  public boolean getLoggedIn() { return this.loggedIn; }

  public void setLoggedIn(boolean loggedIn) { this.loggedIn = loggedIn; }

  public String getUserName() { return this.userName; }

  public void setUserName(String userName) { this.userName = userName; }
}
