package com.sap.sptutorial.auth;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("bouncer")
public class Bouncer {
    public boolean requestEntry() {
        return LocalDateTime.now().getSecond() % 2 == 0;
    }
}
