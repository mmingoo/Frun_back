package Termproject.Termproject2.domain.friend.service;

import Termproject.Termproject2.domain.friend.dto.response.FriendListResponse;
import Termproject.Termproject2.domain.friend.dto.response.FriendResponseDto;
import Termproject.Termproject2.domain.friend.dto.response.UserSearchListResponse;
import Termproject.Termproject2.domain.friend.dto.response.UserSearchResponse;
import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
import Termproject.Termproject2.domain.friend.entity.Friendship;
import Termproject.Termproject2.domain.friend.repository.FriendRequestRepository;
import Termproject.Termproject2.domain.friend.repository.FriendshipRepository;
import Termproject.Termproject2.domain.running.service.RunningLogService;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.service.UserService;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import Termproject.Termproject2.global.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendshipServiceImpl implements FriendShipService {

    private final FriendshipRepository friendshipRepository;
    private final ImageService imageService;
    private final RunningLogService runningLogService;
    private final UserService userService;
    private final FriendRequestService friendRequestService;
    private final FriendRequestRepository friendRequestRepository;

    @Override
    public FriendListResponse getFriendList(Long userId, String cursorName, Long cursorId, int size) {
        // size+1 개 조회 - 실제 필요한 것보다 1개 더 가져와서 다음 페이지 존재 여부 확인
        List<FriendResponseDto> results = friendshipRepository.getFriendList(userId, cursorName, cursorId, size);

        // 조회 결과가 size+1 개면 다음 페이지 존재, size 개만 남기고 마지막 1개 제거
        boolean hasNext = results.size() > size;
        if (hasNext) {
            results = results.subList(0, size);
        }

        // 프로필 이미지 경로 → 완전한 URL로 변환 (BASE_URL + 저장 경로)
        results = results.stream()
                .map(dto -> new FriendResponseDto(
                        dto.getFriendId(),
                        dto.getFriendName(),
                        imageService.getProfileImageUrl(dto.getFriendProfileImage())
                ))
                .collect(Collectors.toList());

        System.out.println("친구 프로필 이미지 : " + results.get(0).getFriendProfileImage());
        // 다음 페이지 조회 시작점(커서)으로 현재 목록의 마지막 항목 사용
        // hasNext가 false면 커서 불필요하므로 null 처리
        FriendResponseDto last = hasNext ? results.get(results.size() - 1) : null;
        return new FriendListResponse(results, hasNext,
                last != null ? last.getFriendId() : null,
                last != null ? last.getFriendName() : null);
    }

    // 유저와 작성자가 친구인지 여부 확인 메서드
    @Override
    public void isFriendWithAuthor(Long userId, Long authorId) {

        Friendship friendship = friendshipRepository.findByUserIdAndAuthorId(userId, authorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FRIEND_WITH_LOG_AUTHOR));

    }


    //TODO: 친구 검색 시 나와의 관계 파악
    @Override
    public UserSearchListResponse searchUsersWithDetailStatus(Long currentUserId, String keyword, String cursorName, Long cursorId, int size) {
        // size+1 개 친구 조회하
        List<User> searchedUsers = userService.findByNicknameContainingWithCursor(keyword, cursorName, cursorId, size + 1);

        // 자신 제외
        List<User> filtered = searchedUsers.stream()
                .filter(user -> !user.getUserId().equals(currentUserId))
                .collect(Collectors.toList());


        // 검색 결과 size 로 다음 친구목록 존재하는지 여부 판단
        boolean hasNext = filtered.size() > size;
        if (hasNext) {
            filtered = filtered.subList(0, size);
        }

        // 친구 검색 결과를 바탕으로 현재 자신과의 친구 관계도 추가하여 표시(요청 보낸 상태, 현재 친구, 아무런 상태도 아닌 경우)
        List<UserSearchResponse> result = filtered.stream()
                .map(targetUser -> {
                    FriendRequestStatus status = determineStatus(currentUserId, targetUser.getUserId());
                    return UserSearchResponse.builder()
                            .userId(targetUser.getUserId())
                            .nickname(targetUser.getNickName())
                            .profileImageUrl(targetUser.getImageUrl())
                            .friendStatus(status)
                            .build();
                })
                .collect(Collectors.toList());


        // 다음 결과가 존재할 때, 다음 검색할 때 기준이 되는 user
        User last = hasNext ? filtered.get(filtered.size() - 1) : null;
        return new UserSearchListResponse(result, hasNext,
                last != null ? last.getUserId() : null,
                last != null ? last.getNickName() : null);
    }

    //TODO: 친구 삭제
    @Override
    @Transactional
    public void unfriend(Long myId, Long friendId) {
        // Friendship 테이블에서 관계 삭제
        long deletedCount = friendshipRepository.deleteFriendship(myId, friendId);

        // deletedCount > 0 : 친구 관계가 끊어진 상태
        // deletedCount == 0 : 애초에 친구관계가 아니거나 삭제된 상태
        if (deletedCount == 0) {
            throw new BusinessException(ErrorCode.NOT_FRIEND); // 친구 상태가 아님
        }
        User sender = findUser(myId);
        User receiver = findUser(friendId);

        // 연관된 친구 신청 기록(ACCEPTED 상태 등)도 삭제하여 초기화
        friendRequestRepository.deleteBySenderAndReceiver(sender, receiver);
        friendRequestRepository.deleteBySenderAndReceiver(receiver, sender);
    }


    //TODO: 친구 관계 상태 확인
    private FriendRequestStatus determineStatus(Long me, Long other) {
        // 이미 친구인지 확인 (양방향 중 하나라도 존재하면 친구)
        if (friendshipRepository.findByIdReceiveUserIdAndIdSenderUserId(me, other).isPresent()
                || friendshipRepository.findByIdReceiveUserIdAndIdSenderUserId(other, me).isPresent()) {
            return FriendRequestStatus.FRIEND;
        }

        // 내가 보낸 요청이 있는지 확인 (내가 sender, 상대가 receiver)
        if (friendRequestService.findByReceiverIdAndSenderId(other, me).isPresent()) {
            return FriendRequestStatus.SENDED;
        }

        // 내가 받은 요청이 있는지 확인 (상대가 sender, 내가 receiver)
        if (friendRequestService.findByReceiverIdAndSenderId(me, other).isPresent()) {
            return FriendRequestStatus.SENDED;
        }

        return FriendRequestStatus.NONE;
    }

    private User findUser(Long userId){
        return userService.findById(userId);
    }

}
