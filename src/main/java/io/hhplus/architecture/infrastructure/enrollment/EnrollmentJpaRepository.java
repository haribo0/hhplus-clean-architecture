package io.hhplus.architecture.infrastructure.enrollment;

import io.hhplus.architecture.domain.enrollment.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentJpaRepository extends JpaRepository<Enrollment, Long> {

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.lecture.id = :lectureId")
    int countByLectureId(@Param("lectureId") Long lectureId);

    boolean existsByUserIdAndLectureId(Long userId, Long lectureId);

    List<Enrollment> findByUserId(Long userId);

    Optional<Enrollment> findByLectureIdAndUserId(Long lectureId, Long userId);

}


