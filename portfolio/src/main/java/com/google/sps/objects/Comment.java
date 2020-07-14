package com.google.sps.objects;

import java.util.Objects;

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
    Comment comment = (Comment)o;
    return Objects.equals(userName, comment.userName) &&
        Objects.equals(commentBody, comment.commentBody) &&
        Objects.equals(imageLink, comment.imageLink);
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
