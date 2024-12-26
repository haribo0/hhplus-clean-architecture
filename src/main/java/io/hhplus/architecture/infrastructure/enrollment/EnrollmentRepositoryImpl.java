package io.hhplus.architecture.infrastructure.enrollment;

import io.hhplus.architecture.domain.enrollment.Enrollment;
import io.hhplus.architecture.domain.enrollment.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EnrollmentRepositoryImpl implements EnrollmentRepository {

    private final EnrollmentJpaRepository enrollmentJpaRepository;

    @Override
    public boolean existsByUserIdAndLectureId(Long userId, Long lectureId) {
        return enrollmentJpaRepository.existsByUserIdAndLectureId(userId, lectureId);
    }

    @Override
    public int countByLectureId(Long lectureId) {
        return enrollmentJpaRepository.countByLectureId(lectureId);
    }

    @Override
    public void save(Enrollment enrollment) {
        enrollmentJpaRepository.save(enrollment);
    }

    @Override
    public Optional<Enrollment> findByLectureIdAndUserId(Long lectureId, Long userId) {
        return enrollmentJpaRepository.findByLectureIdAndUserId(lectureId,userId);
    }


}
