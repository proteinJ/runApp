package com.running.runapp.domain.profile.repository;

import com.running.runapp.domain.profile.domain.Title;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TitleRepository extends JpaRepository<Title, Long> {
    boolean existsByTitleCode(@NotBlank @Size(min = 1, max = 15, message = "칭호 코드는 1~15 글자 이내이어야 합니다.") String s);

    boolean existsById(@NonNull Long titleId);
}
