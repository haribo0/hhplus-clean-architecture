package io.hhplus.architecture.common.exception;

public class LectureFullyBookedException extends IllegalStateException {
    public LectureFullyBookedException(String message) {
        super(message);
    }
}