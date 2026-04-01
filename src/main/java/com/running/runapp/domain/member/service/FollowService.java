package com.running.runapp.domain.member.service;

import com.running.runapp.domain.member.domain.Follow;
import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.dto.SocialRequest;
import com.running.runapp.domain.member.dto.SocialResponse;
import com.running.runapp.domain.member.repository.FollowRepository;
import com.running.runapp.domain.member.repository.MemberRepository;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;

    // 친구 신청
    public SocialResponse.FollowInfo requestFollow(Member me, SocialRequest.FollowSend dto) {
        Member target = memberRepository.findByNickname(dto.targetNickname())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (me.getId().equals(target.getId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 이미 관계있는지 체크
        List<Follow> relations = followRepository.findRelationBetween(me.getId(), target.getId());
        if (!relations.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        Follow follow = Follow.request(me, target);
        Follow saved = followRepository.save(follow);

        return SocialResponse.FollowInfo.builder()
                .followId(saved.getId())
                .followerId(me.getId())
                .followerNickname(me.getNickname())
                .followingId(target.getId())
                .followingNickname(target.getNickname())
                .status(saved.getStatus().name())
                .build();
    }

    // 친구 신청 수락
    public void accept(Member me, String followId) {
        Follow follow = followRepository.findByIdAndFollowing_Id(followId, me.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED));

        if (!follow.isPending()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        follow.accept();
    }

    // 친구 신청 거절
    public void reject(Member me, String followId) {
        Follow follow = followRepository.findByIdAndFollowing_Id(followId, me.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED));

        if (!follow.isPending()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        followRepository.delete(follow);
    }
    public void deleteFollow(Member me, String followId) {
        // 1. 해당 친구 관계가 존재하는지 확인
        Follow follow = followRepository.findById(followId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 친구 관계입니다."));

        // 2. 삭제하려는 사람이 팔로워거나 팔로잉인 경우에만 삭제 가능
        if (!follow.getFollower().getId().equals(me.getId()) &&
                !follow.getFollowing().getId().equals(me.getId())) {
            throw new AccessDeniedException("해당 관계를 삭제할 권한이 없습니다.");
        }

        // 3. DB에서 삭제
        followRepository.delete(follow);
    }
}