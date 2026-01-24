package com.running.runapp.domain.member.repository;

import com.running.runapp.global.security.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    void deleteByKey(String key);
}
