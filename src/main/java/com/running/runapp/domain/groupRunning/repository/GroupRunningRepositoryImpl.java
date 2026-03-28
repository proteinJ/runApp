package com.running.runapp.domain.groupRunning.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.running.runapp.domain.groupRunning.domain.GroupRunning;
import com.running.runapp.domain.groupRunning.dto.GroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import static com.running.runapp.domain.groupRunning.domain.QGroupRunning.groupRunning;

import java.util.List;

@RequiredArgsConstructor
public class GroupRunningRepositoryImpl implements GroupRunningRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<GroupResponse.GroupSummary> findAllByFilter(Pageable pageable) {
//        List<GroupRunning> content = queryFactory
//                .selectFrom(groupRunning)
//                .join(groupRunning.host).fetchJoin()
//                .where(groupRunning.isDeleted)


        return null;
    }
}
