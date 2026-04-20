package Termproject.Termproject2.domain.user.entity;

import Termproject.Termproject2.global.common.basedTime.BaseCreatedEntity;
import Termproject.Termproject2.global.common.basedTime.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "terms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Terms extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long termsId; // 약관 ID

    @Column(nullable = false, length = 50)
    private String termsType; // 약관 유형 구분자

    @Column(nullable = false, length = 200)
    private String title; // 약관 제목

    @Column(nullable = false , length = 2000)
    private String content; // 약관 내용

    @Column(nullable = false)
    private Boolean isRequired; // 필수 동의 여부


}