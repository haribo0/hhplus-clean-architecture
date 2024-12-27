package io.hhplus.architecture.application.enrollment;

import io.hhplus.architecture.application.enrollment.EnrollmentService;
import io.hhplus.architecture.domain.enrollment.Enrollment;
import io.hhplus.architecture.domain.lecture.Lecture;
import io.hhplus.architecture.domain.user.User;
import io.hhplus.architecture.domain.enrollment.EnrollmentRepository;
import io.hhplus.architecture.domain.lecture.LectureRepository;
import io.hhplus.architecture.domain.user.UserRepository;
import io.hhplus.architecture.common.exception.LectureFullyBookedException;
import io.hhplus.architecture.common.exception.InvalidLectureDateException;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class EnrollmentServiceTest {

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private Lecture lecture;

    private static final int MAX_PARTICIPANTS = 30;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock 초기화
        reset(userRepository, lectureRepository, enrollmentRepository);
    }

    @Test
    @DisplayName("유효한 사용자와 강의 ID를 주면 수강 신청에 성공한다")
    void enrollUser_success() {
        // Given
        Long userId = 1L;
        User user = new User(userId,"user1","a@a.com");
        Long lectureId = 10L;

        Lecture lecture = Lecture.builder()
                .id(lectureId)
                .title("Spring Boot 강의")
                .instructor("hailey")
                .startAt(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0))
                .endAt(LocalDateTime.now().plusDays(2).withHour(14).withMinute(0))
                .participants(0)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(enrollmentRepository.existsByUserIdAndLectureId(userId, lectureId)).thenReturn(false);
        when(lectureRepository.findByIdWithLock(lectureId)).thenReturn(Optional.of(lecture));

        // When
        enrollmentService.enrollUser(userId, lectureId);

        // Then
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
        verify(lectureRepository, times(1)).save(lecture);
        assertThat(lecture.getParticipants()).isEqualTo(1); // 수강 인원 증가 확인
    }

    @Test
    @DisplayName("존재하지 않는 사용자를 주면 예외를 던진다")
    void enrollUser_throwsWhenUserNotFound() {
        // Given
        Long userId = 1L;
        Long lectureId = 10L;

        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> enrollmentService.enrollUser(userId, lectureId))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(enrollmentRepository);
    }

    @Test
    @DisplayName("수강 정원이 꽉 찬 강의를 주면 예외를 던진다")
    void enrollUser_throwsWhenLectureFullyBooked() {
        // Given
        Long userId = 1L;
        User user = new User(userId,"user1","a@a.com");
        Long lectureId = 10L;

        Lecture lecture = Lecture.builder()
                .id(lectureId)
                .title("Spring Boot 강의")
                .instructor("hailey")
                .startAt(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0))
                .endAt(LocalDateTime.now().plusDays(2).withHour(14).withMinute(0))
                .participants(MAX_PARTICIPANTS) // 정원에 도달
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lectureRepository.findByIdWithLock(lectureId)).thenReturn(Optional.of(lecture));
        when(enrollmentRepository.existsByUserIdAndLectureId(userId, lectureId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> enrollmentService.enrollUser(userId, lectureId))
                .isInstanceOf(LectureFullyBookedException.class);
    }

    @Test
    @DisplayName("과거 날짜의 강의를 주면 예외를 던진다")
    void enrollUser_throwsWhenInvalidLectureDate() {
        // Given
        Long userId = 1L;
        User user = new User(userId,"user1","a@a.com");
        Long lectureId = 10L;

        Lecture lecture = Lecture.builder()
                .id(lectureId)
                .title("Spring Boot 강의")
                .instructor("hailey")
                .startAt(LocalDateTime.now().minusDays(1).withHour(10).withMinute(0))
                .endAt(LocalDateTime.now().minusDays(1).withHour(14).withMinute(0))
                .participants(0)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(enrollmentRepository.existsByUserIdAndLectureId(userId, lectureId)).thenReturn(false);
        when(lectureRepository.findByIdWithLock(lectureId)).thenReturn(Optional.of(lecture));

        // When & Then
        assertThatThrownBy(() -> enrollmentService.enrollUser(userId, lectureId))
                .isInstanceOf(InvalidLectureDateException.class);
    }
}
