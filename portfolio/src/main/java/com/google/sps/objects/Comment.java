package com.google.sps.objects;

import java.util.Objects;

public final class Comment {
  private final String userName;
  private final String commentBody;
  private final String blobKey;

  public Comment(String userName, String commentBody, String blobKey) {
    this.userName = userName;
    this.commentBody = commentBody;
    this.blobKey = blobKey;
  }

  public String getUserName() { return this.userName; }

  public String getCommentBody() { return this.commentBody; }

  public String getBlobKey() { return this.blobKey; }

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
        Objects.equals(blobKey, comment.blobKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userName, commentBody, blobKey);
  }

  @Override
  public String toString() {
    return "{"
        + " userName='" + getUserName() + "'"
        + ", commentBody='" + getCommentBody() + "'"
        + ", blobKey='" + getBlobKey() + "'"
        + "}";
  }
}
