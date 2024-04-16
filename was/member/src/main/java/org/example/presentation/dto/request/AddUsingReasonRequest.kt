package org.example.presentation.dto.request

import jakarta.validation.constraints.NotNull

class AddUsingReasonRequest(@NotNull val reasons: List<String>?) {
}