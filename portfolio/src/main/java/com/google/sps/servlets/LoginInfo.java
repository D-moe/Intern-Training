package com.google.sps.servlets;

import java.util.Objects;

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

  public boolean isLoggedIn() { return this.loggedIn; }

  public boolean getLoggedIn() { return this.loggedIn; }

  public void setLoggedIn(boolean loggedIn) { this.loggedIn = loggedIn; }

  public String getUserName() { return this.userName; }

  public void setUserName(String userName) { this.userName = userName; }

  public LoginInfo loggedIn(boolean loggedIn) {
    this.loggedIn = loggedIn;
    return this;
  }

  public LoginInfo userName(String userName) {
    this.userName = userName;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof LoginInfo)) {
      return false;
    }
    LoginInfo loginInfo = (LoginInfo)o;
    return loggedIn == loginInfo.loggedIn &&
        Objects.equals(userName, loginInfo.userName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(loggedIn, userName);
  }

  @Override
  public String toString() {
    return "{"
        + " loggedIn='" + isLoggedIn() + "'"
        + ", userName='" + getUserName() + "'"
        + "}";
  }
}
