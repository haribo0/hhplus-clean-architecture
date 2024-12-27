package io.hhplus.architecture.domain.lecture;

import io.hhplus.architecture.common.exception.InvalidLectureDateException;
import io.hhplus.architecture.common.exception.LectureFullyBookedException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lecture {

    private static int MAX_PARTICIPANTS = 30; // 최대 정원

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String instructor;

    private String location;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Builder.Default
    private int participants = 0; // 현재 수강 인원

    /**
     * 수강 신청 가능 여부 확인 및 인원 증가
     */
    public void enroll() {
        validateEnrollment();
        participants++; // 수강 인원 증가
    }

    /**
     * 수강 신청 가능 여부 확인
     */
    private void validateEnrollment() {
        if (participants >= MAX_PARTICIPANTS) {
            throw new LectureFullyBookedException("수강인원이 다 찼습니다.");
        }
        if (startAt.isBefore(LocalDateTime.now())) {
            throw new InvalidLectureDateException("오늘 이후의 강의만 신청가능합니다.");
        }
    }

}
