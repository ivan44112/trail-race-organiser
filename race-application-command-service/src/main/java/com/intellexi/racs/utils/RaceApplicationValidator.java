package com.intellexi.racs.utils;

import com.intellexi.racs.dto.RaceApplicationDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;


@Component
public interface RaceApplicationValidator extends Function<RaceApplicationDto, RaceApplicationValidator.ValidationResult> {
    static RaceApplicationValidator isFirstNameValid() {
        return raceApplication -> raceApplication.getFirstName() != null && !raceApplication.getFirstName().isEmpty() ?
                ValidationResult.SUCCESS : ValidationResult.FIRST_NAME_NOT_VALID;
    }

    static RaceApplicationValidator isLastNameValid() {
        return raceApplication -> raceApplication.getLastName() != null && !raceApplication.getLastName().isEmpty() ?
                ValidationResult.SUCCESS : ValidationResult.LAST_NAME_NOT_VALID;
    }

    static RaceApplicationValidator isClubValid() {
        return raceApplication -> raceApplication.getClub() != null && !raceApplication.getClub().isEmpty() ?
                ValidationResult.SUCCESS : ValidationResult.CLUB_NOT_VALID;
    }

    default RaceApplicationValidator and(RaceApplicationValidator other) {
        return raceApplication -> {
            ValidationResult result = this.apply(raceApplication);
            return result.equals(ValidationResult.SUCCESS) ? other.apply(raceApplication) : result;
        };
    }

    enum ValidationResult {
        SUCCESS,
        FIRST_NAME_NOT_VALID,
        LAST_NAME_NOT_VALID,
        CLUB_NOT_VALID
    }

}
