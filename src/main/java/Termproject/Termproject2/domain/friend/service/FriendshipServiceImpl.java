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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public FriendListResponse getFriendList(Long userId, String cursorName, int size) {
        // size+1 개 조회 - 실제 필요한 것보다 1개 더 가져와서 다음 페이지 존재 여부 확인
        List<FriendResponseDto> results = friendshipRepository.getFriendList(userId, cursorName, size);

        // 조회 결과가 size+1 개면 다음 페이지 존재, size 개만 남기고 마지막 1개 제거
        boolean hasNext = results.size() > size;
        if (hasNext) {
            results = results.subList(0, size);
        }

        // 프로필 이미지 경로 → 완전한 URL로 변환 (BASE_URL + 저장 경로)
        toFullProfileUrl(results);

        // 다음 페이지 조회 시작점(커서)으로 현재 목록의 마지막 항목 사용
        // hasNext가 false면 커서 불필요하므로 null 처리
        FriendResponseDto last = hasNext ? results.get(results.size() - 1) : null;
        return new FriendListResponse(results, hasNext,
                last != null ? last.getFriendName() : null);
    }


    //TODO: 친구 검색 시 나와의 관계 파악
    @Override
    public UserSearchListResponse searchUsersWithDetailStatus(Long currentUserId, String keyword, String cursorName, int size) {
        // size+1 개 조회 - 실제 필요한 것보다 1개 더 가져와서 다음 페이지 존재 여부 확인
        Pageable pageable = PageRequest.of(0, size + 1);
        List<User> searchedUsers = fetchSearchedUsers(keyword, cursorName, pageable);

        // 자신과 비활성 계정 제외
        List<User> filtered = filterSearchedUsers(searchedUsers, currentUserId);

        // 조회 결과가 size+1 개면 다음 페이지 존재, size 개만 남기고 마지막 1개 제거
        boolean hasNext = filtered.size() > size;
        if (hasNext) filtered = filtered.subList(0, size);

        // 대상 유저 ID 목록으로 친구 요청 일괄 조회 후 응답 변환 (N+1 방지)
        List<UserSearchResponse> result = toUserSearchResponseList(filtered, currentUserId);

        // 다음 페이지 조회 시작점(커서)으로 현재 목록의 마지막 항목 사용
        // hasNext가 false면 커서 불필요하므로 null 처리
        User last = hasNext ? filtered.get(filtered.size() - 1) : null;
        return new UserSearchListResponse(result, hasNext,
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

        // FriendRequest 생성
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
        FriendRequest request = findFriendRequest(userId, senderId)
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
        FriendRequest request = findFriendRequest(userId, senderId)
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
        FriendRequest sentReq = findFriendRequest(other, me)
                .orElse(null);
        FriendRequest receivedReq = findFriendRequest(me, other)
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

    // 커서 기반으로 키워드에 해당하는 유저 목록 조회
    private List<User> fetchSearchedUsers(String keyword, String cursorName, Pageable pageable) {
        return cursorName == null
                ? userRepository.findByNickNameContainingNoCursor(keyword, pageable)
                : userRepository.findByNickNameContainingWithCursor(keyword, cursorName, pageable);
    }

    // 검색 결과에서 자신과 비활성 계정 제외
    private List<User> filterSearchedUsers(List<User> users, Long currentUserId) {
        return users.stream()
                .filter(user -> !user.getUserId().equals(currentUserId))
                .filter(user -> user.getUserStatus() == UserStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    // 유저 목록과 친구 상태 맵을 기반으로 검색 응답 목록 변환
    private List<UserSearchResponse> toUserSearchResponseList(List<User> users, Long currentUserId) {
        // 검색된 유저들의 유저 ID 목록 추출
        List<Long> targetIds = users.stream().map(User::getUserId).collect(Collectors.toList());

        // 대상 유저 전체의 친구 상태를 한 번에 조회해서 Map<userId, status>에 저장 (N+1 방지)
        Map<Long, FriendRequestStatus> statusMap = buildStatusMap(currentUserId, targetIds);

        // 각 유저를 응답 DTO로 변환 - 상태 맵에 없으면 NONE(관계 없음)으로 기본값 처리
        return users.stream()
                .map(targetUser -> {
                    FriendRequestStatus status = statusMap.getOrDefault(targetUser.getUserId(), FriendRequestStatus.NONE);
                    return FriendConverter.toUserSearchResponse(
                            targetUser,
                            imageService.getProfileImageUrl(targetUser.getImageUrl()),
                            status);
                })
                .collect(Collectors.toList());
    }

    // 대상 유저 목록에 대한 친구 상태를 한 번에 조회해 Map으로 반환
    private Map<Long, FriendRequestStatus> buildStatusMap(Long me, List<Long> targetIds) {
        // 나와 관련된 친구 요청 일괄 조회
        List<FriendRequest> requests = friendRequestRepository.findAllByMeAndTargetIds(me, targetIds);

        //Map 생성
        Map<Long, FriendRequestStatus> result = new HashMap<>();

        // FriendRequest 당
        for (FriendRequest fr : requests) {
            // 친구 요청의 상대방 결정 : 요청을 받은 사람
            // 내가 보낸 친구요청이라면 otherId는 상대방
            // 내가 받은 친구요청이라면 otherId는 자신
            Long otherId = fr.getSender().getUserId().equals(me)
                    ? fr.getReceiver().getUserId()
                    : fr.getSender().getUserId();

            // unfriend 시 FriendRequest 레코드도 삭제되므로 상대방당 레코드는 항상 1개
            result.put(otherId, resolveStatus(me, fr));
        }
        return result;
    }

    // FriendRequest 하나로부터 me 기준 상태 결정
    private FriendRequestStatus resolveStatus(Long me, FriendRequest fr) {
        // 친구면 FriendRequestStatus.FRIEND 반환
        if (fr.getStatus() == FriendRequestStatus.FRIEND) return FriendRequestStatus.FRIEND;

        // 친구 요청 상태에서
        // sender 가 나일 경우 sended
        // sender 가 내가 아닐 경우 pending
        if (fr.getStatus() == FriendRequestStatus.SENDED) {
            return fr.getSender().getUserId().equals(me)
                    ? FriendRequestStatus.SENDED
                    : FriendRequestStatus.PENDING;
        }
        return FriendRequestStatus.NONE;
    }

    private Optional<FriendRequest> findFriendRequest(Long receiverId, Long senderId) {
        return friendRequestService.findByReceiverIdAndSenderId(receiverId, senderId);
    }


    // 유저 찾기
    private User findUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    // 프로필 full 이미지로 변환
    private void toFullProfileUrl(List<FriendResponseDto> results){
        results.replaceAll(
                dto -> new FriendResponseDto(
                        dto.getFriendId(),
                        dto.getFriendName(),
                        imageService.getProfileImageUrl(dto.getFriendProfileImage())
                ));

    }

}
