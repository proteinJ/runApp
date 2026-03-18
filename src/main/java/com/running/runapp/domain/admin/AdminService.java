package com.running.runapp.domain.admin;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminService {

    private MemberRepository memberRepository;

    public List<Member> getAllMember() {
        memberRepository.findAll();
        return memberRepository.findAll();
    }
}
