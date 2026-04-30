package Termproject.Termproject2.domain.friend.repository;

import Termproject.Termproject2.domain.friend.dto.response.FriendResponseDto;
import Termproject.Termproject2.domain.friend.entity.Friendship;
import Termproject.Termproject2.domain.friend.entity.QFriendship;
import Termproject.Termproject2.domain.user.entity.QUser;
import Termproject.Termproject2.domain.user.entity.UserStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class FriendshipRepositoryImpl implements FriendshipRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    QFriendship friendship = QFriendship.friendship;
    QUser friend = QUser.user;

    //TODO: 친구 목록 반환
    @Override
    public List<FriendResponseDto> getFriendList(Long userId, String cursorName, int size) {

        return queryFactory
                .select(Projections.constructor(FriendResponseDto.class,
                        friend.userId,
                        friend.nickName,
                        friend.imageUrl))
                .from(friendship)
                .join(friend)
                .on(
                        // 내가 sender → receiver가 친구
                        friendship.id.senderUserId.eq(userId)
                                .and(friendship.id.receiveUserId.eq(friend.userId))
                                .or(
                                        // 내가 receiver → sender가 친구
                                        friendship.id.receiveUserId.eq(userId)
                                                .and(friendship.id.senderUserId.eq(friend.userId)))
                )
                .where(
                        friend.userStatus.eq(UserStatus.ACTIVE),
                        cursorName != null ? friend.nickName.gt(cursorName) : null
                )
                .orderBy(friend.nickName.asc())
                .limit(size + 1)
                .fetch();
    }


}

