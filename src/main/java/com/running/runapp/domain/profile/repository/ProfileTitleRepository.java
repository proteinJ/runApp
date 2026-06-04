package com.running.runapp.domain.profile.repository;

import com.running.runapp.domain.profile.domain.Profile;
import com.running.runapp.domain.profile.domain.ProfileTitle;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProfileTitleRepository extends JpaRepository<ProfileTitle, Long> {

    @Query(
            "SELECT pt FROM ProfileTitle pt " +
            "JOIN FETCH pt.profile p " +
            "WHERE p.member.id = :memberId AND pt.title.id = :titleId"
    )
    Optional<ProfileTitle> findWithProfileByMemberIdAndTitleId(@Param("memberId") Long memberId, @Param("titleId") Long titleId);

    boolean existsByProfileAndTitleTitleCode(Profile profile, String titleCode);

    @Query("SELECT pt.title.id FROM ProfileTitle pt WHERE pt.profile.id = :profileId")
    List<Long> findTitleIdsByProfileId(@Param("profileId") Long profileId);
}
