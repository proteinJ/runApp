package com.running.runapp.global.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 실제 테이블 생성 Entity 아님을 명시 + 상속 받는 Entity들은 모두 이것을 가짐.
@EntityListeners(AuditingEntityListener.class) // 엔티티가 DB에 삽입되거나 수정될 때 시간을 자동으로 주입
public abstract class BaseTimeEntity { // 단지 다른 Entity의 속성일뿐 -> abstract

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
