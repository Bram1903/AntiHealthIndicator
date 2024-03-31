package com.deathmotion.antihealthindicator.wrappers;

import com.deathmotion.antihealthindicator.wrappers.interfaces.LoggerWrapper;

import java.util.logging.Logger;

public final class PlatformLoggerWrapperImpl implements LoggerWrapper {

    private final Logger logger = Logger.getLogger("AntiHealthIndicator");

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warning(String message) {
        logger.warning(message);
    }

    @Override
    public void error(String message) {
        logger.severe(message);
    }

    @Override
    public void debug(String message) {
        logger.fine(message);
    }
}