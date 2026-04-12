// src/main/java/Termproject/Termproject2/domain/notification/service/NotificationServiceImpl.java
package Termproject.Termproject2.domain.notification.service;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.friend.entity.FriendRequest;
import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
import Termproject.Termproject2.domain.notification.dto.reponse.NotificationDto;
import Termproject.Termproject2.domain.notification.dto.reponse.NotificationDtos;
import Termproject.Termproject2.domain.notification.entity.Notification;
import Termproject.Termproject2.domain.notification.entity.NotificationType;
import Termproject.Termproject2.domain.notification.repository.NotificationRepository;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.user.entity.User;
import Termproject.Termproject2.global.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ImageService imageService;

    /**
     * 댓글/답글 알림 생성
     * - receiver: 러닝일지 작성자 (알림을 받을 사람)
     * - comment: 작성된 댓글 또는 답글 엔티티
     * 주의: 본인이 본인 글에 댓글을 달 경우 알림을 보내지 않음
     */

    //TODO: 댓글/답글 알림 생성
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void notifyComment(User receiver, Comment comment) {
        // 댓글 작성자와 게시글 작성자가 동일한 경우 알림 생성 X
        if (receiver.getUserId().equals(comment.getUser().getUserId())) {
            return;
        }

        // 답글이면 "답글", 아니면 "댓글"
        boolean isReply = comment.getParent() != null;
        String message = comment.getUser().getNickName() + (isReply ? "님이 답글을 남겼습니다." : "님이 댓글을 남겼습니다.");

        // 댓글/답글 알림 생성
        Notification notification = Notification.builder()
                .user(receiver)
                .type(NotificationType.COMMENT)
                .comment(comment)
                .sender(comment.getUser())
                .message(message)
                .content(getPreview(comment.getContent()))
                .runningLog(comment.getRunningLog())
                .build();

        // 알림 저장
        notificationRepository.save(notification);
    }

    /**
     * 친구 요청 알림 생성
     * - receiver: 친구 요청을 받는 사람
     * - friendRequest: 생성된 친구 요청 엔티티
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void notifyFriendRequest(User receiver, FriendRequest friendRequest, User sender, FriendRequestStatus friendRequestStatus) {
        System.out.println("친구요청 알림 생성");
        // 친구 요청 알림 생성
        Notification notification = Notification.builder()
                .user(receiver)
                .type(NotificationType.FRIEND_REQUEST)
                .friendRequest(friendRequest)
                .sender(sender)
                .message(sender.getNickName() + "님이 친구 요청을 보냈습니다.")
                .friendRequestStatus(friendRequestStatus)
                .build();

        // 친구 요청 알림 저장
        notificationRepository.save(notification);
    }

    /**
     * 좋아요 알림 생성
     * - receiver: 러닝일지 작성자 (알림을 받을 사람)
     * - sender: 좋아요를 누른 사람
     * - runningLog: 좋아요가 눌린 러닝일지
     * 주의: 본인 글에 본인이 좋아요 하는 경우는 LikeService에서 미리 필터링
     * 동일한 (sender, runningLog) 조합에 대해 좋아요 알림은 1번만 발송
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void notifyLike(User receiver, User sender, RunningLog runningLog) {
        // 이미 해당 게시글에 대한 좋아요 알림이 존재하면 스킵
        if (notificationRepository.existsBySenderUserIdAndRunningLogRunningLogIdAndType(
                sender.getUserId(), runningLog.getRunningLogId(), NotificationType.LIKE)) {
            return;
        }

        Notification notification = Notification.builder()
                .user(receiver)
                .sender(sender)
                .runningLog(runningLog)
                .type(NotificationType.LIKE)
                .message(sender.getNickName() + "님이 러닝일지에 좋아요를 눌렀습니다.")
                .runningLog(runningLog)
                .build();

        notificationRepository.save(notification);
    }

    //TODO: 알림 갯수 반환
    @Override
    public long countByUserUserIdAndIsReadFalse(Long userId) {
        return notificationRepository.countByUserUserIdAndIsReadFalse(userId);
    }

    //TODO: 알림 목록 조회
    @Override
    @Transactional
    public NotificationDtos getNotificationList(Long userId, Long lastNotificationId, int size) {
        // 1. 알림 목록 조회
        List<NotificationDto> result = notificationRepository.findByUserUserId(userId, lastNotificationId, PageRequest.of(0, size + 1));

        // 파일명 > 이미지 url 로 변환
        toFullProfileImageUrl(result);

        // 2. 다음 데이터 있는지 여부
        boolean hasNext = hasNext(result, size);

        // 3. size 갯수에 해당하는 데이터만 조회
        result = trimToSize(result, size);

        // 4. 다음에 조회할 cursorId
        Long nextCursorId = getNextCursorId(result, hasNext, NotificationDto::getNotificationId);

        // 5. isRead = false 인 알림들 true 처리
        List<Long> notificationIds = result.stream().map(NotificationDto::getNotificationId).toList();
        if (!notificationIds.isEmpty()) {
            notificationRepository.updateIsReadToTrue(notificationIds);
        }

        return new NotificationDtos(result, nextCursorId, hasNext); // 알림 목록 반환
    }


    // 다음 페이지 존재 여부 판단
    private boolean hasNext(List<?> list, int size) {
        return list.size() > size;
    }

    // size 기준으로 리스트 자르기
    private <T> List<T> trimToSize(List<T> list, int size) {
        return list.size() > size ? list.subList(0, size) : list;
    }

    // next cursor 계산 (제네릭)
    private <T> Long getNextCursorId(List<T> result, boolean hasNext, Function<T, Long> idExtractor) {
        if (!hasNext || result.isEmpty()) {
            return null;
        }
        return idExtractor.apply(result.get(result.size() - 1));
    }


    //댓글용 미리보기, 10글자까지만 미리보기
    public String getPreview(String content){
        if(content.length() > 40) return content.substring(0, 40) + "...";
        else return content;
    }

    @Override
    @Transactional
    public void deleteByComments(List<Comment> comments) {
        if (!comments.isEmpty()) {
            notificationRepository.deleteByCommentIn(comments);
        }
    }

    @Override
    @Transactional
    public void updateFriendRequestNotificationStatus(FriendRequest friendRequest, FriendRequestStatus status) {
        System.out.println("friendRequest : " + friendRequest);
        notificationRepository.findByFriendRequest(friendRequest)
                .ifPresent(notification -> notification.updateFriendRequestStatus(status));
    }

    // 프로필 이미지명 > full 이미지 url
    public void toFullProfileImageUrl(List<NotificationDto> notificationDtoList){
        // 파일명 > 이미지 url 로 변환
        notificationDtoList.forEach(
                dto -> {
                    dto.setProfileImageUrl(imageService.getProfileImageUrl(dto.getUserProfileImageUrl()));
                }
        );
    }
}
