package com.keunsori.keunsoriserver.admin.regularreservation;

import com.keunsori.keunsoriserver.domain.admin.regularreservation.domain.vo.RegularReservationType;
import com.keunsori.keunsoriserver.global.exception.RegularReservationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.INVALID_REGULAR_RESERVATION_TYPE;

public class RegularReservationTypeTest {

    @ParameterizedTest
    @MethodSource("validTypeData")
    void 문자열로부터_정기예약_타입_변환에_성공한다(String input, RegularReservationType expected) {
        RegularReservationType result = RegularReservationType.from(input);
        Assertions.assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> validTypeData() {
        return Stream.of(
                Arguments.of("team", RegularReservationType.TEAM),
                Arguments.of("TEAM", RegularReservationType.TEAM),
                Arguments.of("lesson", RegularReservationType.LESSON),
                Arguments.of("LESSON", RegularReservationType.LESSON)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"teams", "lessons", "TEAMM", "레슨"})
    void 유효하지_않은_정기예약_타입은_예외를_발생시킨다(String input) {
        Assertions.assertThatThrownBy(() -> RegularReservationType.from(input))
                .isInstanceOf(RegularReservationException.class)
                .hasMessage(INVALID_REGULAR_RESERVATION_TYPE);
    }
}
