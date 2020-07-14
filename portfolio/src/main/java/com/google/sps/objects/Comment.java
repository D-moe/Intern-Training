package com.google.sps.objects;

import java.util.Objects;
/**
 * Store the content of a given comment, contents converted into Json using the
 * Gson library.
 */
public final class Comment {
  private final String userName;
  private final String commentBody;

  public Comment(String userName, String commentBody) {
    this.userName = userName;
    this.commentBody = commentBody;
  }

  public String getUserName() { return this.userName; }

  public String getCommentBody() { return this.commentBody; }

  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof Comment)) {
      return false;
    }
    Comment user = (Comment)o;
    return Objects.equals(userName, user.userName) &&
        Objects.equals(commentBody, user.commentBody);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userName, commentBody);
  }

  @Override
  public String toString() {
    return "{"
        + " userName='" + getUserName() + "'"
        + ", commentBody='" + getCommentBody() + "'"
        + "}";
  }
}
