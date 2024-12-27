package io.hhplus.architecture.application.enrollment;

import io.hhplus.architecture.domain.enrollment.Enrollment;
import io.hhplus.architecture.domain.lecture.Lecture;
import io.hhplus.architecture.domain.user.User;
import io.hhplus.architecture.domain.lecture.LectureRepository;
import io.hhplus.architecture.domain.enrollment.EnrollmentRepository;
import io.hhplus.architecture.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService {

    private final LectureRepository lectureRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    @Transactional
    public Enrollment enrollUser(Long userId, Long lectureId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Lecture lecture = lectureRepository.findByIdWithLock(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 강의입니다."));

        if (enrollmentRepository.existsByUserIdAndLectureId(userId, lectureId)) {
            throw new IllegalArgumentException("이미 수강한 강의입니다.");
        }
        lecture.enroll();

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .lecture(lecture)
                .build();
        enrollmentRepository.save(enrollment);

        // just in case 비관적락 사용으로 변경 감지 못 할 경우 저장
        lectureRepository.save(lecture);

        return enrollment;
    }



}
