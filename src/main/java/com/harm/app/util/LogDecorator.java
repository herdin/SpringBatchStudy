package com.harm.app.util;

import org.slf4j.Logger;

public class LogDecorator {

    public static void decorateLine(Logger log, String format, Object... arguments) {
        log.debug("=======================================================");
        log.debug(format, arguments);
        log.debug("=======================================================");
    }
}
