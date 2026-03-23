package com.porlio.porliobe.module.notification.service;

public interface NotificationService {

  void sendVerificationEmail(String toEmail, String username, String verifyToken);

  void sendPasswordResetEmail(String toEmail, String username, String resetToken);

  void sendWelcomeEmail(String toEmail, String username);
}
