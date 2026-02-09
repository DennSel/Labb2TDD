package com.example;

import net.bytebuddy.build.ToStringPlugin;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
            "room, 1337-01-29T13:37,", // end time null
            "room,, 1337-01-29T13:38" // start time null
    })
    void throwExceptionIfAParameterIsNullWhenBookRoom (String roomId, LocalDateTime timeNow, LocalDateTime timeInFuture){
        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, timeNow, timeInFuture))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void returnsTrueIfBookingIsCreatedSuccessfully(){
        String roomId = "room";
        LocalDateTime timeNow = LocalDateTime.of(1337, 1, 29, 12, 0);
        LocalDateTime timeInFuture = timeNow.plusHours(1);

        // Mock
        Room room = mock(Room.class);
        when(room.isAvailable(timeNow, timeInFuture)).thenReturn(true);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(timeProvider.getCurrentTime()).thenReturn(timeNow);

        boolean result = bookingSystem.bookRoom(roomId, timeNow, timeInFuture);

        assertThat(result).isTrue();
        verify(room).addBooking(any(Booking.class));
        verify(roomRepository).save(room);
    }

    @Test
    void returnsFalseIfRoomIsUnavailable() {
        String roomId = "room";
        LocalDateTime timeNow = LocalDateTime.of(1337, 1, 29, 12, 0);
        LocalDateTime timeInFuture = timeNow.plusHours(1);

        Room room = mock(Room.class);
        when(timeProvider.getCurrentTime()).thenReturn(timeNow);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(room.isAvailable(timeNow, timeInFuture)).thenReturn(false);

        boolean result = bookingSystem.bookRoom(roomId, timeNow, timeInFuture);

        assertThat(result).isFalse();
    }

    @Test
    void returnTrueIfBookingSuccessfulWhenNotificationFails() throws NotificationException {
        String roomId = "room";
        LocalDateTime timeNow = LocalDateTime.of(1337, 1, 29, 12, 0);
        LocalDateTime timeInFuture = timeNow.plusHours(1);

        Room room = mock(Room.class);
        when(timeProvider.getCurrentTime()).thenReturn(timeNow);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(room.isAvailable(timeNow, timeInFuture)).thenReturn(false);
        doThrow(new NotificationException(""))
                .when(notificationService).sendBookingConfirmation(any(Booking.class));

        boolean result = bookingSystem.bookRoom(roomId, timeNow, timeInFuture);

        assertThat(result).isTrue();
        verify(room).addBooking(any(Booking.class)); // Make sure booking is created
        verify(roomRepository).save(room); // Make sure room is saved
    }

    @Test
    void returnOnlyAvailableRooms () {
        LocalDateTime timeNow = LocalDateTime.of(1337, 1, 29, 12, 0);
        LocalDateTime timeInFuture = timeNow.plusHours(1);

        Room room1 = mock(Room.class);
        Room room2 = mock(Room.class);
        Room room3 = mock(Room.class);
        Room room4 = mock(Room.class);

        when(room1.isAvailable(timeNow, timeInFuture)).thenReturn(true);
        when(room2.isAvailable(timeNow, timeInFuture)).thenReturn(false);
        when(room3.isAvailable(timeNow, timeInFuture)).thenReturn(false);
        when(room4.isAvailable(timeNow, timeInFuture)).thenReturn(true);

        when(roomRepository.findAll()).thenReturn(List.of(room1, room2, room3, room4));

        List<Room> result = bookingSystem.getAvailableRooms(timeNow,timeInFuture);

        assertThat(result)
                .containsExactly(room1, room4)
                .hasSize(2);
    }

    @ParameterizedTest
    @CsvSource({
            "1337-01-29T13:37,", // start time null
            ",1337-01-29T13:38"// end time null
    })
    void throwExceptionIfAParameterIsNullWhenGetAvailableRooms (LocalDateTime timeNow, LocalDateTime timeInFuture){
        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(timeNow, timeInFuture))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Måste ange både start- och sluttid");
    }

    @Test
    void throwExceptionIfEndTimeIsBeforeStartTimeWhenGetAvailableRooms(){
        LocalDateTime timeNow = LocalDateTime.of(1337, 1, 29, 12, 0);
        LocalDateTime timeInFuture = timeNow.plusHours(1);

        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(timeInFuture, timeNow))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sluttid måste vara efter starttid");
    }


    // CANCEL BOOKING //
    @Test
    void throwExceptionIfParameterIsNullWhenCancelBooking() {
        assertThatThrownBy(() -> bookingSystem.cancelBooking(null))
                .hasMessageContaining("Boknings-id kan inte vara null");
    }

    @Test
    void returnsTrueIfBookingIsCancelledSuccessfully () throws NotificationException {
        // Set up data
        String bookingId = "bookingid";
        LocalDateTime timeNow = LocalDateTime.of(1337, 1, 29, 12, 0);
        LocalDateTime timeInFuture = timeNow.plusHours(1);

        // Mock room with booking
        Room room = mock(Room.class);
        Booking booking = new Booking(bookingId, "roomid", timeNow, timeInFuture);

        // Make sure the mocked room returns the booking
        when(room.hasBooking(bookingId)).thenReturn(true);
        when(room.getBooking(bookingId)).thenReturn(booking);

        // Repository should return mocked room
        when(roomRepository.findAll()).thenReturn(List.of(room));

        // Self-explanatory
        when(timeProvider.getCurrentTime()).thenReturn(timeNow);

        // Test the method!
        boolean result = bookingSystem.cancelBooking(bookingId);

        // Verify result
        assertThat(result).isTrue(); // True if canceled
        verify(room).removeBooking(bookingId); // Make sure booking removed form room (could not have been there ever though)
        verify(roomRepository).save(room); // Make sure room saved to repository
        verify(notificationService).sendCancellationConfirmation(booking); // Make sure notification sent
    }

}
