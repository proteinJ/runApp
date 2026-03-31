package com.running.runapp.domain.groupRunning.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.running.runapp.domain.groupRunning.domain.GroupRunning;
import com.running.runapp.domain.groupRunning.dto.GroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.running.runapp.domain.groupRunning.domain.QGroupRunning.groupRunning;

@RequiredArgsConstructor
public class GroupRunningRepositoryCustomImpl implements GroupRunningRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<GroupResponse.GroupSummary> findAllByFilter(Pageable pageable) {
        List<GroupRunning> content = queryFactory
                .selectFrom(groupRunning)
                .join(groupRunning.host).fetchJoin()
                .orderBy(groupRunning.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        List<GroupResponse.GroupSummary> dtos = content.stream()
                .map(GroupResponse.GroupSummary::from)
                .toList();

        return new SliceImpl<>(dtos, pageable, hasNext);
    }
}
