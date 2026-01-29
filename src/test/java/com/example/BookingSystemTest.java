package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class BookingSystemTest {

    @Mock
    TimeProvider timeProvider;

    @Mock
    RoomRepository roomRepository;

    @Mock
    NotificationService notificationService;

    @InjectMocks
    BookingSystem bookingSystem;

    @Test
    void throwExceptionWhenStartTimeIsNull() {
        LocalDateTime startTimeNull = null;
        LocalDateTime endTimeNow = LocalDateTime.now();
        assertThatThrownBy(() -> bookingSystem.bookRoom("1", startTimeNull, endTimeNow))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    @Test
    void throwExceptionWhenStartTimeIsBeforeCurrentTime() {
        LocalDateTime timeNow = LocalDateTime.of(1337, 01, 29, 12, 0);
        LocalDateTime timeInPast = timeNow.minusHours(1);

        when(timeProvider.getCurrentTime()).thenReturn(timeNow);

        assertThatThrownBy(() -> bookingSystem.bookRoom("1", timeInPast, timeNow))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Kan inte boka tid i dåtid");
    }
}
