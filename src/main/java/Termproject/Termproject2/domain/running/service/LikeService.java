package Termproject.Termproject2.domain.running.service;

public interface LikeService {
    //TODO: 러닝일지 좋아요
    void addLike(Long userId, Long runningLogId);

    //TODO: 러닝일지 좋아요 취소
    void removeLike(Long userId, Long runningLogId);
}
