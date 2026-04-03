package Termproject.Termproject2.domain.friend.entity;

public enum FriendRequestStatus {
    SENDED,
    FRIEND,
    NONE,
    PENDING // SENDED 일 때 내가 receiver 이면 PENDING
}
