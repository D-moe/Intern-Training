package com.google.sps.objects;

import java.util.Objects;
/**
 * Store the content of a given comment, contents converted into Json using the
 * Gson library.
 */
public final class Comment {
  private final String userName;
  private final String commentBody;
  private final String imageLink;

public Comment(String userName, String commentBody, String imageLink) {
    this.userName = userName;
    this.commentBody = commentBody;
    this.imageLink = imageLink;
  }

  public String getUserName() { return this.userName; }

  public String getCommentBody() { return this.commentBody; }

  public String getImageLink() { return this.imageLink; }

  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof Comment)) {
      return false;
    }
    Comment user = (Comment)o;
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
