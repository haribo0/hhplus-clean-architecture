package io.hhplus.architecture.domain.lecture;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LectureRepository {

    Optional<Lecture> findById(Long lectureId);

    boolean existsById(Long userId);

    List<Lecture> getLecturesByDate(LocalDateTime startAt, LocalDateTime endAt);

    Optional<Lecture> findByIdWithLock(Long id);

    void save(Lecture lecture);

}
