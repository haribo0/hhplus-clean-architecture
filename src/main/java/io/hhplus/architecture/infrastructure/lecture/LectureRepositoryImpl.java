package io.hhplus.architecture.infrastructure.lecture;

import io.hhplus.architecture.domain.lecture.Lecture;
import io.hhplus.architecture.domain.lecture.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class LectureRepositoryImpl implements LectureRepository {

    private final LectureJpaRepository lectureJpaRepository;

    @Override
    public Optional<Lecture> findById(Long lectureId) {
        return lectureJpaRepository.findById(lectureId);
    }


    @Override
    public boolean existsById(Long lectureId) {
        return lectureJpaRepository.existsById(lectureId);
    }

    /**
     * 특정 날짜 범위 내의 강의 목록을 반환
     * @param startAt 시작 시간
     * @param endAt 종료 시간
     * @return 해당 범위의 강의 목록
     */
    @Override
    public List<Lecture> getLecturesByDate(LocalDateTime startAt, LocalDateTime endAt) {
        return lectureJpaRepository.findAllByStartAtBetween(startAt,endAt);
    }

    /**
     * 특정 ID를 가진 강의를 조회 - PESSIMISTIC_WRITE 락을 사용
     * @param id 강의 ID
     * @return 락이 걸린 상태의 강의 객체(Optional)
     */
    @Override
    public Optional<Lecture> findByIdWithLock(Long id) {
        return lectureJpaRepository.findByIdWithLock(id);
    }

    @Override
    public void save(Lecture lecture) {
        lectureJpaRepository.save(lecture);
    }

}
