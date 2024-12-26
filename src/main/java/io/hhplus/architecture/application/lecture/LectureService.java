package io.hhplus.architecture.application.lecture;

import io.hhplus.architecture.domain.enrollment.Enrollment;
import io.hhplus.architecture.domain.enrollment.EnrollmentRepository;
import io.hhplus.architecture.domain.lecture.Lecture;
import io.hhplus.architecture.domain.lecture.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;
    private final EnrollmentRepository enrollmentRepository;


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

    /**
     * 유저의 수강 강의 목록을 반환
     * @param userId 유저아이디
     * @return 수강신청한 특강 목록
     */
    public List<Lecture> getUserEnrolledLectures(Long userId) {
        return enrollmentRepository.findByUserId(userId).stream()
                .map(Enrollment::getLecture)
                .collect(Collectors.toList());
    }


}
