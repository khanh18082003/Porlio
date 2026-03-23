package com.porlio.porliobe.module.shared.data.constant;

public enum SectionType {
  /**
   * Hero section: Tên, title, bio, avatar, location. Mỗi portfolio chỉ có đúng 1 hero (enforced bởi
   * partial unique index trong DB).
   */
  HERO,

  /**
   * Skills section: Danh sách kỹ năng theo nhóm.
   */
  SKILLS,

  /**
   * Experience section: Kinh nghiệm làm việc.
   */
  EXPERIENCE,

  /**
   * Education section: Học vấn.
   */
  EDUCATION,

  /**
   * Projects section: Dự án cá nhân (manual hoặc import từ GitHub).
   */
  PROJECTS,

  /**
   * Contact section: Thông tin liên hệ và mạng xã hội.
   */
  CONTACT
}
