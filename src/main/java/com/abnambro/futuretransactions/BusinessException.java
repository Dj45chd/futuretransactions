package com.abnambro.futuretransactions;

import java.io.IOException;

class BusinessException extends RuntimeException {
    public BusinessException(String string, IOException ex) {
        super(string, ex);
    }
}
