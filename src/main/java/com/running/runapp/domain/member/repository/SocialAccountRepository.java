package com.running.runapp.domain.member.repository;

import com.running.runapp.domain.member.domain.Provider;
import com.running.runapp.domain.member.domain.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    Optional<SocialAccount> findByProviderAndProviderUserId(Provider provider, String providerUserId);
}