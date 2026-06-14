package com.running.runapp.domain.profile.service;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.notification.service.NotificationService;
import com.running.runapp.domain.profile.domain.Follow;
import com.running.runapp.domain.profile.domain.Profile;
import com.running.runapp.domain.profile.dto.SocialRequest;
import com.running.runapp.domain.profile.dto.SocialResponse;
import com.running.runapp.domain.profile.repository.FollowRepository;
import com.running.runapp.domain.profile.repository.ProfileRepository;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final FollowRepository followRepository;
    private final ProfileRepository profileRepository;
    private final NotificationService notificationService;

    // 친구 신청
    public SocialResponse.FollowInfo requestFollow(Member me, SocialRequest.FollowSend dto) {
        Profile myProfile = me.getProfile();
        Profile target = profileRepository.findByNickname(dto.targetNickname())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (myProfile.getId().equals(target.getId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 이미 관계있는지 체크
        List<Follow> relations = followRepository.findRelationBetween(myProfile.getId(), target.getId());
        if (!relations.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        Follow follow = Follow.request(myProfile, target);
        Follow saved = followRepository.save(follow);
        notificationService.createFollowRequest(target.getMember(), me, saved.getId());

        return SocialResponse.FollowInfo.builder()
                .followId(saved.getId())
                .followerId(me.getId())
                .followerNickname(myProfile.getNickname())
                .followingId(target.getMember().getId())
                .followingNickname(target.getNickname())
                .status(saved.getStatus().name())
                .build();
    }

    // 친구 신청 수락
    public void accept(Member me, String followId) {
        Follow follow = followRepository.findByIdAndFollowing_Id(followId, me.getProfile().getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED));

        if (!follow.isPending()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        follow.accept();
        notificationService.createFollowAccepted(follow.getFollower().getMember(), me, follow.getId());
    }

    // 친구 신청 거절
    public void reject(Member me, String followId) {
        Follow follow = followRepository.findByIdAndFollowing_Id(followId, me.getProfile().getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED));

        if (!follow.isPending()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        followRepository.delete(follow);
    }
    @Transactional(readOnly = true)
    public List<SocialResponse.FollowMemberSummary> getPendingRequests(Long memberId) {
        return followRepository.findPendingRequestsByMemberId(memberId).stream()
                .map(f -> new SocialResponse.FollowMemberSummary(
                        f.getId(),
                        f.getFollower().getMember().getId(),
                        f.getFollower().getNickname()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SocialResponse.FollowMemberSummary> getFollowers(Long memberId) {
        return followRepository.findFollowersByMemberId(memberId).stream()
                .map(f -> new SocialResponse.FollowMemberSummary(
                        f.getId(),
                        f.getFollower().getMember().getId(),
                        f.getFollower().getNickname()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SocialResponse.FollowMemberSummary> getFollowings(Long memberId) {
        return followRepository.findFollowingsByMemberId(memberId).stream()
                .map(f -> new SocialResponse.FollowMemberSummary(
                        f.getId(),
                        f.getFollowing().getMember().getId(),
                        f.getFollowing().getNickname()
                ))
                .toList();
    }

    public void deleteFollow(Member me, String followId) {
        Follow follow = followRepository.findById(followId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED));

        Long myProfileId = me.getProfile().getId();
        if (!follow.getFollower().getId().equals(myProfileId) &&
                !follow.getFollowing().getId().equals(myProfileId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        followRepository.delete(follow);
    }
}
