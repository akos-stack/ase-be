package com.bloxico.userservice.util.resttemplate;

import com.bloxico.userservice.exceptions.GsonParsingException;
import com.bloxico.userservice.exceptions.RestTemplateException;
import com.bloxico.userservice.util.gson.GsonResponseParserUtil;
import com.bloxico.userservice.web.model.BaseRestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * This wrapper class is used for easier handling of http requests
 */
@Component
@Slf4j
public class RestTemplateUtil {

    private GsonResponseParserUtil gsonResponseParserUtil;
    private RestTemplate restTemplate;

    @Autowired
    public RestTemplateUtil(GsonResponseParserUtil gsonResponseParserUtil, RestTemplate restTemplate) {
        this.gsonResponseParserUtil = gsonResponseParserUtil;
        this.restTemplate = restTemplate;
    }

    public Object get(String url, Class<? extends BaseRestResponse> responseClass) throws RestTemplateException {
        log.debug("Sending get request to url: {}", url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        Object responseObject = handleResponse(response, responseClass);
        return responseObject;
    }

    public Object getWithRequestEntity(String url, HttpEntity<String> entity, Class<? extends BaseRestResponse> responseClass) throws RestTemplateException {
        log.debug("Sending get request to url: {}  - with entity.", url);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        Object responseObject = handleResponse(response, responseClass);
        return responseObject;
    }

    public Object post(String url, HttpEntity<String> entity, Class<? extends BaseRestResponse> responseClass) throws RestTemplateException {
        log.debug("Sending post request to url: {}", url);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        Object responseObject = handleResponse(response, responseClass);
        return responseObject;
    }

    public void postWithoutResponseObject(String url, HttpEntity<String> entity, String errorMessage) throws RestTemplateException {
        log.debug("Sending post request to url: {}", url);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if(response.getStatusCode().value() != 200) {
            throw new RestTemplateException(errorMessage);
        }
    }

    private Object handleResponse(ResponseEntity<String> response, Class<? extends BaseRestResponse> responseClass) throws RestTemplateException {

        BaseRestResponse responseClassInstance;
        try {
            responseClassInstance = responseClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("Failed to instantiate BaseResponse class! - {} ", responseClass.getSimpleName());
            //TODO How to handle this?
            throw new RuntimeException();
        }

        if (response.getStatusCode().value() == HttpStatus.OK.value()) {
            try {
                return gsonResponseParserUtil.parseJson(response.getBody(), responseClass);
            } catch (GsonParsingException e) {
                throw new RestTemplateException(responseClassInstance.getErrorMessage());
            }
        } else {
            throw new RestTemplateException(responseClassInstance.getErrorMessage());
        }
    }
}
