package com.example;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class BookingSystemTest {

    @Mock
    TimeProvider timeProvider;

    @Mock
    RoomRepository roomRepository;

    @Mock
    NotificationService notificationService;

    @InjectMocks
    BookingSystem bookingSystem;

    @Test
    void throwExceptionWhenStartTimeIsBeforeCurrentTime() {
        String roomId = "";
        LocalDateTime timeNow = LocalDateTime.of(1337, 1, 29, 12, 0);
        LocalDateTime timeInPast = timeNow.minusHours(1);

        when(timeProvider.getCurrentTime()).thenReturn(timeNow);

        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, timeInPast, timeNow))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Kan inte boka tid i dåtid");
    }

    @Test
    void throwExceptionWhenEndTimeIsBeforeStartTime() {
        String roomId = "";
        LocalDateTime timeNow = LocalDateTime.of(1337, 1, 29, 12, 0);
        LocalDateTime timeInPast = timeNow.minusHours(1);

        when(timeProvider.getCurrentTime()).thenReturn(timeNow);

        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, timeNow, timeInPast))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sluttid måste vara efter starttid");
    }

    @Test
    void throwExceptionIfRoomDoesntExist() {
        String roomId = "Non-existent room id";
        LocalDateTime timeNow = LocalDateTime.of(1337, 1, 29, 12, 0);
        LocalDateTime timeInFuture = timeNow.plusDays(1);

        when(timeProvider.getCurrentTime()).thenReturn(timeNow);

        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, timeNow, timeInFuture))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rummet existerar inte");
    }

    @ParameterizedTest
    @CsvSource({
            ", 1337-01-29T13:37, 1337-01-29T13:38", // roomId null
            "room, 1337-01-29T13:37,", // roomId null
            "room,, 1337-01-29T13:38" // roomId null
    })
    void throwExceptionIfAParameterIsNull (String roomId, LocalDateTime timeNow, LocalDateTime timeInFuture){
        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, timeNow, timeInFuture))
                .isInstanceOf(IllegalArgumentException.class);
    }


}
