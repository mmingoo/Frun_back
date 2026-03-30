package Termproject.Termproject2.domain.running.converter;


import Termproject.Termproject2.domain.running.dto.response.FriendFeedResponseDto;
import Termproject.Termproject2.domain.running.entity.RunningLog;
import Termproject.Termproject2.domain.running.entity.RunningLogImage;
import Termproject.Termproject2.domain.user.entity.User;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class RunningLogConverter {

    public static FriendFeedResponseDto RunningLogToFriendFeedResponseDto(
            RunningLog runningLog,
            User author
    ){
        return FriendFeedResponseDto.builder()
                .runningLogId(runningLog.getRunningLogId())
                .userId(author.getUserId())
                .nickName(author.getNickName())
                .imageUrl(author.getImageUrl())
                .runDate(runningLog.getRunDate())
                .distance(runningLog.getDistance())
                .pace(runningLog.getPace())
                .duration(runningLog.getDuration())
                .memo(runningLog.getMemo())
                .createdAt(runningLog.getCreatedAt())
                .commentCtn(runningLog.getCommentCtn())
                .likeCtn(runningLog.getLikeCtn())
                .logImages(
                        runningLog.getImages().stream()
                                .map(RunningLogImage::getImageUrl)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
