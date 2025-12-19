package com.albaraka.digital.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminUpdateUserStatusRequest {

    @NotNull
    private Boolean active;
}