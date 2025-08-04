package com.cankasikci.rest.test;

public class TestConfig {

  public static final String BASE_URI = "https://petstore.swagger.io/v2";

  public static final long WAIT_MILLIS = 5000;

  public static final int CREATE_PET_ID = 888;

  public static final int UPDATE_PET_ID = 389;

  public static final int NON_EXISTING_PET_ID = 99999999;

  public static void waitStep() {
    try {
      Thread.sleep(WAIT_MILLIS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
