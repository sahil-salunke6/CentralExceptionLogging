package org.practice.logging.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CustomLogConfig {

    String projectName = new File(System.getProperty("user.dir")).getName();

    @PostConstruct
    public void init() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern(
                "%cyan([%d{yyyy-MM-dd HH:mm:ss.SSS}]) " +           // Timestamp in cyan (sky blue)
                        "%highlight(%-5level) " +                           // Level in green/yellow/red/etc.
                        "[traceId=%X{traceId}] " +                          // Trace ID (default color)
                        "%blue([" + projectName + "]) " +                   // Project name in blue
                        "%cyan(%class -> %M) " +                         // File → Class in cyan
                        ":%line " +                                         // Line number (default color)
                        "%yellow(error=%X{errorCode}) " +                 // errorCode in yellow
                        "%msg%n"                                            // Message (default terminal/IDE color)
        );
        encoder.start();

        // Appender
        ConsoleAppender consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(context);
        consoleAppender.setEncoder(encoder);
        consoleAppender.start();

        // Root logger
        Logger rootLogger = context.getLogger("ROOT");
        rootLogger.detachAndStopAllAppenders();
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(consoleAppender);
    }
}
