package io.hhplus.architecture.application.lecture;

import io.hhplus.architecture.domain.lecture.Lecture;
import io.hhplus.architecture.domain.lecture.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;

    /**
     * 특정 날짜의 강의 목록을 반환
     * @param date 필터링할 날짜
     * @return 해당 날짜의 강의 목록
     */
    public List<Lecture> getLecturesByDate(LocalDate date) {
        LocalDateTime startDateTime = date.atStartOfDay();
        LocalDateTime endDateTime = date.atTime(23, 59, 59);
        return lectureRepository.getLecturesByDate(startDateTime, endDateTime);
    }


}
