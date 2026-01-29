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
import java.util.Optional;

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
        String roomId = "";
        LocalDateTime timeIsNull = null;
        LocalDateTime timeNow = LocalDateTime.now();

        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, timeIsNull, timeNow))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    @Test
    void throwExceptionWhenStartTimeIsBeforeCurrentTime() {
        String roomId = "";
        LocalDateTime timeNow = LocalDateTime.of(1337, 01, 29, 12, 0);
        LocalDateTime timeInPast = timeNow.minusHours(1);

        when(timeProvider.getCurrentTime()).thenReturn(timeNow);

        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, timeInPast, timeNow))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Kan inte boka tid i dåtid");
    }

    @Test
    void throwExceptionWhenEndTimeIsBeforeStartTime() {
        String roomId = "";
        LocalDateTime timeNow = LocalDateTime.of(1337, 01, 29, 12, 0);
        LocalDateTime timeInPast = timeNow.minusHours(1);

        when(timeProvider.getCurrentTime()).thenReturn(timeNow);

        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, timeNow, timeInPast))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sluttid måste vara efter starttid");
    }

    @Test
    void throwExceptionIfRoomDoesntExist() {
        String roomId = "Non-existent room id";
        LocalDateTime timeNow = LocalDateTime.of(1337, 01, 29, 12, 0);
        LocalDateTime timeInFuture = timeNow.plusDays(1);

        when(timeProvider.getCurrentTime()).thenReturn(timeNow);

        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, timeNow, timeInFuture))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rummet existerar inte");
    }
}
