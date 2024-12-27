package io.hhplus.architecture.interfaces.lecture.api;

import io.hhplus.architecture.domain.lecture.Lecture;

import java.time.LocalDateTime;

public record LectureResponse(
        Long id,
        String title,
        String description,
        String instructor,
        String location,
        LocalDateTime startAt,
        LocalDateTime endAt,
        int participants
) {
    // Converts Lecture entity to LectureResponse
    public static LectureResponse fromEntity(Lecture lecture) {
        return new LectureResponse(
                lecture.getId(),
                lecture.getTitle(),
                lecture.getDescription(),
                lecture.getInstructor(),
                lecture.getLocation(),
                lecture.getStartAt(),
                lecture.getEndAt(),
                lecture.getParticipants()
        );
    }
}
