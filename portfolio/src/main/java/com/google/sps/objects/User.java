package com.google.sps.objects;

import java.util.Objects;

public final class User {
  private final String userName;
  private final String commentBody;
  private final String imageLink;

  public User(String userName, String commentBody, String imageLink) {
    this.userName = userName;
    this.commentBody = commentBody;
    this.imageLink = imageLink;
  }

  public String getUserName() { return this.userName; }

  public String getImageLink() { return this.imageLink; }

  public String getCommentBody() { return this.commentBody; }

  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof User)) {
      return false;
    }
    User user = (User)o;
    return Objects.equals(userName, user.userName) &&
        Objects.equals(commentBody, user.commentBody) &&
        Objects.equals(imageLink, user.imageLink);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userName, commentBody, imageLink);
  }

  @Override
  public String toString() {
    return "{"
        + " userName='" + getUserName() + "'"
        + ", commentBody='" + getCommentBody() + "'"
        + ", imageLink='" + getImageLink() + "'"
        + "}";
  }
}