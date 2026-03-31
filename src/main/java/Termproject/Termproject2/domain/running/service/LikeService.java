package Termproject.Termproject2.domain.running.service;

public interface LikeService {
    void addLike(Long userId, Long runningLogId);
    void removeLike(Long userId, Long runningLogId);
}
