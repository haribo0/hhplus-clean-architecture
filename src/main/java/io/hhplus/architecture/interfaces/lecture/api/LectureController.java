package io.hhplus.architecture.interfaces.lecture.api;

import io.hhplus.architecture.application.enrollment.EnrollmentService;
import io.hhplus.architecture.application.lecture.LectureService;
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
    private final EnrollmentService enrollmentService;


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

    /**
     * 강의 신청 API
     * @param lectureId 강의 ID
     * @param userId 사용자 ID
     * @return 신청 성공 여부 메시지
     */
    @PostMapping("/{lectureId}/enroll")
    public ResponseEntity<String> enrollLecture(@PathVariable Long lectureId, @RequestParam Long userId) {
        try {
            enrollmentService.enrollUser(userId, lectureId);
            return ResponseEntity.ok("수강신청이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 신청 완료된 강의 목록 조회 API
     * @param userId 사용자 ID
     * @return 신청 완료된 강의 목록
     */
    @GetMapping("/enrolled")
    public ResponseEntity<List<Lecture>> getUserEnrolledLectures(@PathVariable Long userId) {
        List<Lecture> lectures = lectureService.getUserEnrolledLectures(userId);
        return ResponseEntity.ok(lectures);
    }

}
