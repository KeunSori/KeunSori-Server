package com.keunsori.keunsoriserver.reservation.domain;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.INVALID_RESERVATION_TYPE;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.global.exception.ReservationException;

import java.util.stream.Stream;

public class ReservationTypeTest {

    @ParameterizedTest
    @MethodSource("reservationConvertTestData")
    public void 문자열로부터_변환에_성공한다(String actual, ReservationType expected) {
        // given

        // when
        ReservationType reservationType = ReservationType.from(actual);

        // then
        Assertions.assertThat(reservationType).isEqualTo(expected);
    }

    public static Stream<Arguments> reservationConvertTestData() {
        return Stream.of(
                Arguments.of("personal", ReservationType.PERSONAL),
                Arguments.of("PERSONAL", ReservationType.PERSONAL),
                Arguments.of("team", ReservationType.TEAM),
                Arguments.of("TEAM", ReservationType.TEAM),
                Arguments.of("lesson", ReservationType.LESSON),
                Arguments.of("LESSON", ReservationType.LESSON)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"teams", "personal2", "lessons"})
    public void enum에_없는_값이라면_변환에_실패한다(String actual) {
        // given

        // when & then
        Assertions.assertThatThrownBy(
                        () -> ReservationType.from(actual))
                .isInstanceOf(ReservationException.class)
                .hasMessage(INVALID_RESERVATION_TYPE);
    }
}
