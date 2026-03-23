package com.porlio.porliobe.module.notification.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailMessage {

  String to;
  String subject;
  String htmlContent;

  // Metadata để log và debug
  String type;       // "VERIFY_EMAIL", "RESET_PASSWORD"
  String userId;     // để trace log
}
