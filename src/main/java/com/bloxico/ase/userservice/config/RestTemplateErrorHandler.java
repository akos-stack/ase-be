package com.bloxico.ase.userservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * Since responses will be handled externally, this handler is used to ignore internal RestTemplate error handling.
 */
@Slf4j
@Component
public class RestTemplateErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().value() != HttpStatus.OK.value();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        log.error("Response error | statusCode: {}, statusText: {}",
                response.getStatusCode(), response.getStatusText());
    }

}
