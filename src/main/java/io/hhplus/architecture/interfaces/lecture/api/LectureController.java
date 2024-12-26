package io.hhplus.architecture.interfaces.lecture.api;

import io.hhplus.architecture.domain.lecture.Lecture;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/v1/lectures")
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;


    /**
     * 날짜별 강의 목록 조회 API
     * @param date 날짜
     * @return 신청 가능한 강의 목록
     */
    @GetMapping("/")
    public ResponseEntity<List<Lecture>> getAvailableLectures(LocalDate date) {
        List<Lecture> lectures = lectureService.getLecturesByDate(date);
        return ResponseEntity.ok(lectures);
    }

}
