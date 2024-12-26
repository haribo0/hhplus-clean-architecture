package io.hhplus.architecture.domain.enrollment;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository {
    boolean existsByUserIdAndLectureId(Long userId, Long lectureId);

    int countByLectureId(Long lectureId);

    void save(Enrollment enrollment);

    Optional<Enrollment> findByLectureIdAndUserId(Long lectureId, Long userId);

}
