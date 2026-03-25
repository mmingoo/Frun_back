//package Termproject.Termproject2.domain.friend.repository;
//
//import Termproject.Termproject2.domain.friend.QFriendship;
//import Termproject.Termproject2.domain.friend.dto.FriendResponseDto;
//import Termproject.Termproject2.domain.user.entity.QUser;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import lombok.RequiredArgsConstructor;
//
//import java.util.List;
//
//@RequiredArgsConstructor
//public class FriendshipRepositoryImpl implements FriendshipRepositoryCustom{
//
//    private final JPAQueryFactory queryFactory;
//
//    public List<FriendResponseDto> getFriendList(Long userId){
//
//        QFriendship friendship = QFriendship.friendship;
//        return queryFactory
//                .select(member.id);
//
//    }
//}
