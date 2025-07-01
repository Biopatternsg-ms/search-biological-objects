package com.biopatternsg.domain.exceptions;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
@AllArgsConstructor
public class ApiException extends RuntimeException  {
    @Serial
    private static final long serialVersionUID = 725940427332549109L;
    private final GeneralError generalError;
    private Integer customCode;
    private String customMessage;

    public ApiException(GeneralError generalError) {
        this.generalError = generalError;
    }
}
