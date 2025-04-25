package com.prash.social_media_platform;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordCheck {
  public static void main(String[] args) {
    String raw  = "test1234";
    String hash = "$2a$10$HVnJd04KEEppJzfCwSNXZOMGHw6FF2ZOMeWGPoAmyp2ShcGxKP2.S";

    BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
    boolean ok = enc.matches(raw, hash);
    System.out.println("Password match? " + ok);
  }
}
