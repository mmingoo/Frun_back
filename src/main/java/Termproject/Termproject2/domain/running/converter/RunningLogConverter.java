package Termproject.Termproject2.domain.running.converter;

import Termproject.Termproject2.domain.running.dto.request.RunningLogCreateRequest;
import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.entity.Like;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.running.entity.RunningLogImage;
import Termproject.Termproject2.domain.user.entity.User;

import java.time.LocalTime;
import java.util.List;

public class RunningLogConverter {

    // TODO: 러닝 로그 엔티티 변환 메서드
    public static RunningLog toRunningLog(User user, RunningLogCreateRequest request,
                                          LocalTime duration, String pace) {
        return RunningLog.builder()
                .user(user)
                .runDate(request.getRunDate())
                .duration(duration)
                .distance(request.getDistance())
                .memo(request.getMemo())
                .isPublic(request.isPublic())
                .pace(pace)
                .build();
    }

    // TODO: 러닝 로그 이미지 엔티티 변환 메서드
    public static RunningLogImage toRunningLogImage(RunningLog runningLog, String fileName) {
        return RunningLogImage.builder()
                .runningLog(runningLog)
                .imageUrl(fileName)
                .build();
    }

    // TODO: 좋아요 엔티티 변환 메서드
    public static Like toLike(User user, RunningLog runningLog) {
        return Like.builder()
                .user(user)
                .runningLog(runningLog)
                .build();
    }

    // TODO: 친구 피드 응답 DTO 변환 메서드 (toFriendFeedResponseDto)
    public static FriendFeedResponseDto toFriendFeedResponseDto(RunningLog runningLog,
                                                                String profileImageUrl,
                                                                List<String> imageUrls,
                                                                boolean liked,
                                                                int commentCtn) {
        User author = runningLog.getUser();
        FriendFeedResponseDto dto = new FriendFeedResponseDto(
                runningLog.getRunningLogId(), author.getUserId(), author.getNickName(),
                profileImageUrl,
                runningLog.getRunDate(), runningLog.getRunTime(), runningLog.getDistance(),
                runningLog.getPace(), runningLog.getDuration(), runningLog.getMemo(),
                runningLog.getCreatedAt(), commentCtn, runningLog.getLikeCtn(),
                liked, imageUrls, runningLog.isPublic()
        );
        dto.setLiked(liked);
        return dto;
    }
}