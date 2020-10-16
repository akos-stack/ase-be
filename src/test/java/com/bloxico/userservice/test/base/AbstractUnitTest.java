package com.bloxico.userservice.test.base;

import com.bloxico.AppEntry;
import com.bloxico.userservice.util.MockUtil;
import com.bloxico.userservice.web.model.ApiError;
import com.google.gson.Gson;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("test")
@SpringBootTest(classes = {AppEntry.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(SpringExtension.class)
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public abstract class AbstractUnitTest {

    @Autowired
    protected Gson gson;

    @Autowired
    protected MockUtil mockUtil;

    @Value("${api.url}")
    protected String API_URL;

    protected ApiError extractApiErrorFromResponse(Response response) {

        ResponseBody body = response.getBody();

        return gson.fromJson(body.asString(), ApiError.class);
    }
}
