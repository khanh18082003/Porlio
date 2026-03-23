package com.porlio.porliobe.module.shared.data.response;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.domain.Page;

public record PageResult<T>(List<T> content, PageMeta pagination) implements Serializable {

  public record PageMeta(int page,
                         int size,
                         long totalElements,
                         int totalPages,
                         boolean hasNext,
                         boolean hasPrevious
  ) implements Serializable {

  }

  public static <T> PageResult<T> of(Page<T> page) {
    return new PageResult<>(
        page.getContent(),
        new PageMeta(
            page.getNumber() + 1,
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.hasNext(),
            page.hasPrevious()
        )
    );
  }
}
