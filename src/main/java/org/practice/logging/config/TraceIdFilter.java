package org.practice.logging.config;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            Span span = Span.current();
            SpanContext ctx = span.getSpanContext();
            if (ctx.isValid()) {
                MDC.put("traceId", ctx.getTraceId());
                MDC.put("spanId", ctx.getSpanId());
            }
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
