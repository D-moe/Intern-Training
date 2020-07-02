package com.google.sps.objects;

import java.util.Objects;

public final class User {
  private final String userName;
  private final String commentBody;

  public User(String userName, String commentBody) {
    this.userName = userName;
    this.commentBody = commentBody;
  }

  public String getUserName() { return this.userName; }

  public String getCommentBody() { return this.commentBody; }


  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(userName, user.userName) && Objects.equals(commentBody, user.commentBody);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userName, commentBody);
  }

  @Override
  public String toString() {
    return "{" +
      " userName='" + getUserName() + "'" +
      ", commentBody='" + getCommentBody() + "'" +
      "}";
  }
}
