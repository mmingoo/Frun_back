// src/main/java/Termproject/Termproject2/domain/notification/service/NotificationServiceImpl.java
package Termproject.Termproject2.domain.notification.service;

import Termproject.Termproject2.domain.comment.Comment;
import Termproject.Termproject2.domain.friend.entity.FriendRequestStatus;
import Termproject.Termproject2.domain.notification.converter.NotificationConverter;
import Termproject.Termproject2.domain.notification.dto.reponse.NotificationDto;
import Termproject.Termproject2.domain.notification.dto.reponse.NotificationDtos;
import Termproject.Termproject2.domain.notification.dto.request.SelectedNotificationRequestDto;
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

        // 댓글/답글 알림 생성 및 저장
        notificationRepository.save(NotificationConverter.toCommentNotification(
                receiver, comment, message, getPreview(comment.getContent())));
    }

    //TODO: 친구 요청 알림 생성
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void notifyFriendRequest(User receiver, User sender, FriendRequestStatus friendRequestStatus) {
        notificationRepository.save(NotificationConverter.toFriendRequestNotification(
                receiver, sender,
                sender.getNickName() + "님이 친구 요청을 보냈습니다.",
                friendRequestStatus));
    }

    //TODO: 좋아요 알림 생성 (중복 방지, 본인 글 제외)
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void notifyLike(User receiver, User sender, RunningLog runningLog) {
        // 이미 해당 게시글에 대한 좋아요 알림이 존재하면 스킵
        if (notificationRepository.existsBySenderUserIdAndRunningLogRunningLogIdAndType(
                sender.getUserId(), runningLog.getRunningLogId(), NotificationType.LIKE)) {
            return;
        }

        // 알림 생성
        notificationRepository.save(NotificationConverter.toLikeNotification(
                receiver, sender, runningLog,
                sender.getNickName() + "님이 러닝일지에 좋아요를 눌렀습니다."));
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
        List<NotificationDto> result = notificationRepository.findNotificationByUserUserId(userId, lastNotificationId, PageRequest.of(0, size + 1));

        // 파일명 > 이미지 url 로 변환
        toFullProfileImageUrl(result);

        // 비활성화 계정 발신자 마스킹
        maskInactiveSenders(result);

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


    //TODO: 댓글 목록에 연관된 알림 삭제
    @Override
    @Transactional
    public void deleteByComments(List<Comment> comments) {
        if (!comments.isEmpty()) {
            notificationRepository.deleteByCommentIn(comments);
        }
    }


    //TODO: 친구 요청 수락 알림 생성
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void notifyFriendRequestAccepted(User sender, User receiver) {
        notificationRepository.save(NotificationConverter.toFriendRequestAcceptedNotification(
                sender, receiver,
                receiver.getNickName() + "님이 친구 요청을 수락했습니다."));
    }

    //TODO: 친구 요청 거절 알림 생성
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void notifyFriendRequestRejected(User sender, User receiver) {
        notificationRepository.save(NotificationConverter.toFriendRequestRejectedNotification(
                sender, receiver,
                receiver.getNickName() + "님이 친구 요청을 거절했습니다."));
    }

    //TODO: 선택 알림 삭제
    @Override
    @Transactional
    public void deleteSelectedNotification(Long userId, SelectedNotificationRequestDto selectedNotificationRequestDto) {
        notificationRepository.deleteSelectedNotification(userId, selectedNotificationRequestDto.getSelectedNotificationIds());
    }

    //TODO: 전체 알림 삭제
    @Override
    @Transactional
    public void deleteAllNotification(Long userId) {
        notificationRepository.deleteAllByUserId(userId);
    }

    //TODO: 친구 요청 알림 상태 업데이트 (수락/거절 반영)
    @Override
    @Transactional
    public void updateFriendRequestNotificationStatus(Long senderUserId, Long receiverUserId, FriendRequestStatus status) {
        notificationRepository.findLatestBySenderUserIdAndUserUserIdAndType(senderUserId, receiverUserId, NotificationType.FRIEND_REQUEST)
                .ifPresent(notification -> notification.updateFriendRequestStatus(status));
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
    private String getPreview(String content){
        if(content.length() > 40) return content.substring(0, 40) + "...";
        else return content;
    }


    // 프로필 이미지명 > full 이미지 url
    private void toFullProfileImageUrl(List<NotificationDto> notificationDtoList){
        // 파일명 > 이미지 url 로 변환
        notificationDtoList.forEach(
                dto -> {
                    dto.setProfileImageUrl(imageService.getProfileImageUrl(dto.getUserProfileImageUrl()));
                }
        );
    }

    // 비활성화 계정 마스킹 처리
    private void maskInactiveSenders(List<NotificationDto> notificationDtoList) {
        notificationDtoList.forEach(dto -> {
            // 유저 정보가 없거나 활성화이면 건너띔
            if (dto.getSenderStatus() == null || !dto.getSenderStatus().isInactive()) return;

            // 신고 수락 or 거절인 경우에도 건너띔
            if (dto.getType() == NotificationType.REPORT_ACCEPTED || dto.getType() == NotificationType.REPORT_REJECTED) return;

            // 프로필 사진 null 처리
            dto.setProfileImageUrl(null);

            // 마스킹 메세지 처리
            dto.setMessage(buildMaskedMessage(dto.getType()));
        });
    }

    // 마스크 메시지 생성
    private String buildMaskedMessage(NotificationType type) {
        return switch (type) {
            case COMMENT                  -> "비활성화 계정님이 댓글을 남겼습니다.";
            case LIKE                     -> "비활성화 계정님이 러닝일지에 좋아요를 눌렀습니다.";
            case FRIEND_REQUEST           -> "비활성화 계정님이 친구 요청을 보냈습니다.";
            case FRIEND_REQUEST_ACCEPTED  -> "비활성화 계정님이 친구 요청을 수락했습니다.";
            case FRIEND_REQUEST_REJECTED  -> "비활성화 계정님이 친구 요청을 거절했습니다.";
            default                       -> "비활성화 계정의 알림입니다.";
        };
    }
}
