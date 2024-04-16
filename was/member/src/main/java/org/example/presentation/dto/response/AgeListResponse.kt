package org.example.presentation.dto.response

import jakarta.validation.constraints.NotNull
import org.example.support.NotBlankList

class AgeListResponse(@NotNull @NotBlankList val ages: List<String>?) {
}