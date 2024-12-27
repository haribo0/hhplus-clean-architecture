package io.hhplus.architecture.domain.enrollment;

import io.hhplus.architecture.domain.lecture.Lecture;
import io.hhplus.architecture.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table(uniqueConstraints={
    @UniqueConstraint(
            name = "uniqueLectureAndUser",
        columnNames = {"lectureId","userId"}
    ),
})
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lectureId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Lecture lecture;

    @Builder.Default
    private LocalDateTime enrolledAt = LocalDateTime.now();

}
