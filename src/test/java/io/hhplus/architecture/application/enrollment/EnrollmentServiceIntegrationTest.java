package io.hhplus.architecture.application.enrollment;

import io.hhplus.architecture.application.enrollment.EnrollmentService;
import io.hhplus.architecture.domain.lecture.Lecture;
import io.hhplus.architecture.domain.user.User;
import io.hhplus.architecture.domain.enrollment.EnrollmentRepository;
import io.hhplus.architecture.domain.lecture.LectureRepository;
import io.hhplus.architecture.domain.user.UserRepository;
import io.hhplus.architecture.infrastructure.enrollment.EnrollmentJpaRepository;
import io.hhplus.architecture.infrastructure.lecture.LectureJpaRepository;
import io.hhplus.architecture.infrastructure.user.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@ActiveProfiles("test")
public class EnrollmentServiceIntegrationTest {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LectureJpaRepository lectureJpaRepository;

    @Autowired
    private EnrollmentJpaRepository enrollmentJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private Lecture lecture;

    private static final int MAX_CAPACITY = 30;

    @BeforeAll
    void setUpBeforeAll() {

        lectureJpaRepository.deleteAll();
        enrollmentJpaRepository.deleteAll();

        // 테스트 유저 생성 (한 번만 실행)
        userJpaRepository.deleteAll(); // 기존 데이터 삭제
        List<User> users = IntStream.rangeClosed(1, 50)
                .mapToObj(i -> new User().builder().name("user" + i).email("a@a.com"+i).build())
                .collect(Collectors.toList());
        userRepository.saveAll(users); // 한 번에 저장

    }

    @BeforeEach
    void setUp() {
        lectureJpaRepository.deleteAll();
        // 테스트 강의 데이터 생성
        lecture = Lecture.builder()
                .title("Spring Boot 강의")
                .instructor("hailey")
                .startAt(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0))
                .endAt(LocalDateTime.now().plusDays(2).withHour(14).withMinute(0))
                .participants(0)
                .build();
        lectureRepository.save(lecture); // DB에 저장
    }

    @AfterEach
    void tearDown() {
        // 수강신청 초기화
        enrollmentJpaRepository.deleteAll();
    }

    @AfterAll
    void tearDownAll() {
        // 테스트 후 데이터 삭제
        lectureJpaRepository.deleteAll();
        enrollmentJpaRepository.deleteAll();
    }


    @Test
    @DisplayName("40명이 동시 요청할 경우에도 정원만큼만 수강신청된다. (비관적락)")
    void testPessimisticLockWithConcurrentRequests() throws InterruptedException {
        // Given
        Long lectureId = lecture.getId();
        int threadCount = 30;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // When
        for (int i = 0; i < threadCount; i++) {
            final Long userId = (long) (i + 1);
            executorService.execute(() -> {
                try {
                    log.info("userId: {}",userId);
                    enrollmentService.enrollUser(userId, lectureId);
                } catch (Exception e) {
                    log.error("Error for userId {}: {}", userId, e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        Lecture updatedLecture = lectureRepository.findById(lectureId).orElseThrow();
        int enrolledCount = enrollmentRepository.countByLectureId(lectureId);

        assertThat(updatedLecture.getParticipants()).isEqualTo(MAX_CAPACITY);
        assertThat(enrolledCount).isEqualTo(MAX_CAPACITY);
    }


    @Test
    @DisplayName("같은 유저가 수강신청을 동시에 5번 하면 한번만 신청되고 나머지는 실패한다.")
    void testDuplicateEnrollmentBySameUser() throws InterruptedException {
        // Given
        Long lectureId = lecture.getId();
        Long userId = 1L;
        int threadCount = 5;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    enrollmentService.enrollUser(userId, lectureId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    log.error("Error for userId {}: {}", userId, e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        int successExpected = 1;
        int failExpected = threadCount - successExpected;
        assertThat(successCount.get()).isEqualTo(successExpected);
        assertThat(failCount.get()).isEqualTo(failExpected);
        boolean isEnrolled = enrollmentRepository.existsByUserIdAndLectureId(userId, lectureId);
        assertThat(isEnrolled).isTrue();
    }

}