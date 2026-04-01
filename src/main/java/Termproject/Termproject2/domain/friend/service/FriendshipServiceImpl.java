package Termproject.Termproject2.domain.friend.service;

import Termproject.Termproject2.domain.friend.dto.response.FriendListResponse;
import Termproject.Termproject2.domain.friend.dto.response.FriendResponseDto;
import Termproject.Termproject2.domain.friend.dto.response.UserSearchResponse;
import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
import Termproject.Termproject2.domain.friend.entity.Friendship;
import Termproject.Termproject2.domain.friend.repository.FriendshipRepository;
import Termproject.Termproject2.domain.running.service.RunningLogService;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.service.UserService;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import Termproject.Termproject2.global.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendShipService {

    private final FriendshipRepository friendshipRepository;
    private final ImageService imageService;
    private final RunningLogService runningLogService;
    private final UserService userService;
    private final FriendRequestService friendRequestService;

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

    @Override
    public List<UserSearchResponse> searchUsersWithDetailStatus(Long currentUserId, String keyword, Pageable pageable) {
        // 검색한 유저 조회하기
        Page<User> searchedUsers = userService.findByNicknameContaining(keyword, pageable);


        return searchedUsers.getContent().stream()
                .filter(user -> !user.getUserId().equals(currentUserId)) // 포함 결과에 자신 제외
                .map(targetUser -> {

                    //현재 나와의 상태 (SENDED,RECEIVED,NONE)
                    FriendRequestStatus status = determineStatus(currentUserId, targetUser.getUserId());

                    return UserSearchResponse.builder()
                            .userId(targetUser.getUserId())
                            .nickname(targetUser.getNickName())
                            .profileImageUrl(targetUser.getImageUrl())
                            .friendStatus(status)
                            .build();
                })
                .collect(Collectors.toList());
    }


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
            return FriendRequestStatus.RECEIVED;
        }

        return FriendRequestStatus.NONE;
    }

}
