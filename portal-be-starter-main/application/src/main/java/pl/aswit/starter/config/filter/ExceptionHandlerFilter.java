package pl.aswit.starter.config.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.val;

import static pl.aswit.starter.model.error.ErrorCode.GENERIC_ERROR_CODE;
import static pl.aswit.starter.rest.config.ControllerErrorHandler.makeResponseError;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper mapper;

    public ExceptionHandlerFilter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            val errorResponse = makeResponseError(GENERIC_ERROR_CODE, String.format("Unpredicted, not caught exception occurred: %s", e.getMessage()), e);
            response.setStatus(INTERNAL_SERVER_ERROR.value());
            response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(mapper.writeValueAsString(errorResponse));
        }
    }
}
