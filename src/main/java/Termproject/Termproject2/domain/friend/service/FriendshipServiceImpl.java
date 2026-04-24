package Termproject.Termproject2.domain.friend.service;

import Termproject.Termproject2.domain.friend.converter.FriendConverter;
import Termproject.Termproject2.domain.friend.dto.request.FriendRequestDto;
import Termproject.Termproject2.domain.friend.dto.response.FriendListResponse;
import Termproject.Termproject2.domain.friend.dto.response.FriendResponseDto;
import Termproject.Termproject2.domain.friend.dto.response.UserSearchListResponse;
import Termproject.Termproject2.domain.friend.dto.response.UserSearchResponse;
import Termproject.Termproject2.domain.friend.entity.FriendRequest;
import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
import Termproject.Termproject2.domain.friend.entity.Friendship;
import Termproject.Termproject2.domain.friend.repository.FriendRequestRepository;
import Termproject.Termproject2.domain.friend.repository.FriendshipRepository;
import Termproject.Termproject2.domain.notification.service.NotificationService;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.domain.user.entity.UserStatus;
import Termproject.Termproject2.domain.user.repository.UserRepository;
import Termproject.Termproject2.global.common.response.ErrorCode;
import Termproject.Termproject2.global.exception.BusinessException;
import Termproject.Termproject2.global.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendshipServiceImpl implements FriendShipService {

    private final FriendshipRepository friendshipRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final FriendRequestService friendRequestService;
    private final FriendRequestRepository friendRequestRepository;

    private final NotificationService notificationService;


    //TODO: 친구 목록 커서 기반 조회
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


    //TODO: 친구 검색 시 나와의 관계 파악
    @Override
    public UserSearchListResponse searchUsersWithDetailStatus(Long currentUserId, String keyword, String cursorName, Long cursorId, int size) {
        // size+1 개 친구 조회하
        Pageable pageable = PageRequest.of(0, size + 1);
        List<User> searchedUsers = (cursorName == null || cursorId == null)
                ? userRepository.findByNickNameContainingNoCursor(keyword, pageable)
                : userRepository.findByNickNameContainingWithCursor(keyword, cursorName, cursorId, pageable);

        // 자신 제외 + 비활성화 계정 제외
        List<User> filtered = searchedUsers.stream()
                .filter(user -> !user.getUserId().equals(currentUserId))
                .filter(user -> user.getUserStatus() == UserStatus.ACTIVE)
                .collect(Collectors.toList());


        // 검색 결과 size 로 다음 친구목록 존재하는지 여부 판단
        boolean hasNext = filtered.size() > size;
        if (hasNext) filtered = filtered.subList(0, size);


        // 대상 유저 ID 목록으로 친구 요청 일괄 조회 (N+1 방지)
        List<Long> targetIds = filtered.stream().map(User::getUserId).collect(Collectors.toList());
        Map<Long, FriendRequestStatus> statusMap = buildStatusMap(currentUserId, targetIds);

        List<UserSearchResponse> result = filtered.stream()
                .map(targetUser -> {
                    FriendRequestStatus status = statusMap.getOrDefault(targetUser.getUserId(), FriendRequestStatus.NONE);
                    return FriendConverter.toUserSearchResponse(
                            targetUser,
                            imageService.getProfileImageUrl(targetUser.getImageUrl()),
                            status);
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
        int deletedCount = friendshipRepository.deleteFriendship(myId, friendId);

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



    //TODO: 친구 요청
    @Transactional
    public void sendFriendRequest(Long userId, Long friendId) {
        User sender = findUser(userId);
        User receiver = findUser(friendId);

        // 이미 내가 보낸 요청이 있는지 (중복 요청 방지)
        if (friendRequestRepository.findByReceiver_UserIdAndSender_UserId(friendId, userId).isPresent()) {
            throw new BusinessException(ErrorCode.ALREADY_FRIEND_REQUESTED);
        }

        // 상대가 나에게 이미 요청을 보낸 상태인지 (양방향 요청 방지)
        if (friendRequestRepository.findByReceiver_UserIdAndSender_UserId(userId, friendId).isPresent()) {
            throw new BusinessException(ErrorCode.ALREADY_RECEIVED_REQUEST);
        }

        // 이미 친구인지
        if (friendshipRepository.findByUserIdAndAuthorId(userId, friendId).isPresent()) {
            throw new BusinessException(ErrorCode.ALREADY_FRIEND);
        }

        FriendRequest request = FriendConverter.toFriendRequest(sender, receiver);

        // 친구 요청 전송
        friendRequestRepository.save(request);
        // receiver 입장에서의 상태 계산 (receiver가 me, sender가 other → PENDING)
        FriendRequestStatus statusForReceiver = getStatus(receiver.getUserId(), sender.getUserId());
        // 요청을 받는 상대방에게 친구 요청 알림 전송
        notificationService.notifyFriendRequest(receiver, sender, statusForReceiver);
    }

    //TODO : 친구 요청 수락
    @Transactional
    public void acceptFriendRequest(Long senderId, Long userId) {
        FriendRequest request = friendRequestService.findByReceiverIdAndSenderId(userId, senderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));


        // SENDED 만 처리할 수 있도록
        if (request.getStatus() != FriendRequestStatus.SENDED) {
            throw new BusinessException(ErrorCode.REQUEST_COMPLETED);
        }

        // 친구 요청 상태 변경
        request.setStatus(FriendRequestStatus.FRIEND);

        // Friendship 테이블에 저장
        Friendship friendship = FriendConverter.toFriendship(request.getSender(), request.getReceiver());

        friendshipRepository.save(friendship);

        // 친구 요청 알림 상태 업데이트 (PENDING → FRIEND)
        notificationService.updateFriendRequestNotificationStatus(request.getSender().getUserId(), request.getReceiver().getUserId(), FriendRequestStatus.FRIEND);

        // 요청을 보낸 사람에게 수락 알림 전송
        notificationService.notifyFriendRequestAccepted(request.getSender(), request.getReceiver());
    }


    //TODO: 친구 요청 거절
    @Transactional
    public void rejectFriendRequest(Long senderId, Long userId) {
        FriendRequest request = friendRequestService.findByReceiverIdAndSenderId(userId, senderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        // SENDED 상태만 거절 가능
        if (request.getStatus() != FriendRequestStatus.SENDED) {
            throw new BusinessException(ErrorCode.REQUEST_COMPLETED);
        }

        // 친구 요청 알림 상태 업데이트 (PENDING → REJECTED)
        notificationService.updateFriendRequestNotificationStatus(request.getSender().getUserId(), request.getReceiver().getUserId(), FriendRequestStatus.REJECTED);

        // 요청을 보낸 사람에게 거절 알림 전송
        notificationService.notifyFriendRequestRejected(request.getSender(), request.getReceiver());

        // 친구 요청 데이터 삭제
        friendRequestRepository.delete(request);
    }

    //TODO: 친구 관계 상태 확인
    private FriendRequestStatus determineStatus(Long me, Long other) {

        FriendRequestStatus status = getStatus(me,other);

        return status;
    }

    //TODO: 친구 수 조회
    @Override
    public long getFriendCount(Long targetUserId){
        return friendshipRepository.countByUserId(targetUserId);
    }

    //TODO: 두 유저 간 친구 요청 상태 조회
    @Override
    public FriendRequestStatus getStatus(Long me, Long other) {
        // 1. 예외를 던지지 않고 null을 허용하도록 변경
        FriendRequest sentReq = friendRequestService.findByReceiverIdAndSenderId(other, me)
                .orElse(null);
        FriendRequest receivedReq = friendRequestService.findByReceiverIdAndSenderId(me, other)
                .orElse(null);

        // 2. 상태 추출, 객체가 없으면 NONE으로 간주
        FriendRequestStatus sentStatus = (sentReq != null) ? sentReq.getStatus() : FriendRequestStatus.NONE;
        FriendRequestStatus receivedStatus = (receivedReq != null) ? receivedReq.getStatus() : FriendRequestStatus.NONE;

        // 3. 우선순위 판별
        if (sentStatus == FriendRequestStatus.FRIEND || receivedStatus == FriendRequestStatus.FRIEND) {
            return FriendRequestStatus.FRIEND;
        }

        // 친구요청이 존재하는데 내가 보낸 경우에 SENDED 이면 SENDED
        if (sentStatus == FriendRequestStatus.SENDED) return FriendRequestStatus.SENDED;

        // 친구요청이 존재하는데 내가 받은 경우에 SENDED 이면 PENDING
        if (receivedStatus == FriendRequestStatus.SENDED) return FriendRequestStatus.PENDING;

        return FriendRequestStatus.NONE;
    }

    // 대상 유저 목록에 대한 친구 상태를 한 번에 조회해 Map으로 반환
    private Map<Long, FriendRequestStatus> buildStatusMap(Long me, List<Long> targetIds) {
        List<FriendRequest> requests = friendRequestRepository.findAllByMeAndTargetIds(me, targetIds);
        Map<Long, FriendRequestStatus> result = new java.util.HashMap<>();
        for (FriendRequest fr : requests) {
            Long otherId = fr.getSender().getUserId().equals(me)
                    ? fr.getReceiver().getUserId()
                    : fr.getSender().getUserId();
            FriendRequestStatus existing = result.get(otherId);
            FriendRequestStatus current = resolveStatus(me, fr);
            if (existing == null || hasPriority(current, existing)) {
                result.put(otherId, current);
            }
        }
        return result;
    }

    // FriendRequest 하나로부터 me 기준 상태 결정
    private FriendRequestStatus resolveStatus(Long me, FriendRequest fr) {
        if (fr.getStatus() == FriendRequestStatus.FRIEND) return FriendRequestStatus.FRIEND;
        if (fr.getStatus() == FriendRequestStatus.SENDED) {
            return fr.getSender().getUserId().equals(me)
                    ? FriendRequestStatus.SENDED
                    : FriendRequestStatus.PENDING;
        }
        return FriendRequestStatus.NONE;
    }

    // 상태 우선순위: FRIEND > SENDED > PENDING > NONE
    private boolean hasPriority(FriendRequestStatus a, FriendRequestStatus b) {
        List<FriendRequestStatus> order = List.of(
                FriendRequestStatus.FRIEND, FriendRequestStatus.SENDED,
                FriendRequestStatus.PENDING, FriendRequestStatus.NONE);
        return order.indexOf(a) < order.indexOf(b);
    }

    // 유저 찾기
    private User findUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

}
