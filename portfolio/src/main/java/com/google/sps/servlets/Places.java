package com.google.sps.servlets;

import java.util.Objects;

public class Places {
  private Double latitude;
  private Double longitude;
  private String name;

  public Places() {}

  public Places(Double latitude, Double longitude, String name) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.name = name;
  }

  public Double getLatitude() { return this.latitude; }

  public void setLatitude(Double latitude) { this.latitude = latitude; }

  public Double getLongitude() { return this.longitude; }

  public void setLongitude(Double longitude) { this.longitude = longitude; }

  public String getName() { return this.name; }

  public void setName(String name) { this.name = name; }

  public Places latitude(Double latitude) {
    this.latitude = latitude;
    return this;
  }

  public Places longitude(Double longitude) {
    this.longitude = longitude;
    return this;
  }

  public Places name(String name) {
    this.name = name;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof Places)) {
      return false;
    }
    Places places = (Places)o;
    return Objects.equals(latitude, places.latitude) &&
        Objects.equals(longitude, places.longitude) &&
        Objects.equals(name, places.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(latitude, longitude, name);
  }

  @Override
  public String toString() {
    return "{"
        + " latitude='" + getLatitude() + "'"
        + ", longitude='" + getLongitude() + "'"
        + ", name='" + getName() + "'"
        + "}";
  }
}