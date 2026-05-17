package com.running.runapp.domain.profile.service;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.profile.dto.TitleResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TitleService {

    public List<TitleResponse.allTitle> getAllTitle(Member member) {
        // 개발 중
        List<TitleResponse.allTitle> titleResList = new ArrayList<>();

        return titleResList;
    }
}
