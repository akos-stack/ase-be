package com.bloxico.userservice.util.gson;

import com.bloxico.userservice.exceptions.GsonParsingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Date;

@Component
@Slf4j
public class GsonResponseParserUtil {

    public Object parseJson(String response, Class clazz) throws GsonParsingException {
        try {

            Gson gson = initializeViaGsonBuilderForClass(clazz);

            Type type = TypeToken.getParameterized(clazz).getType();
            Object o = gson.fromJson(response, type);

            return o;

            //Since any exception means that something went wrong with parsing - throw Exception
        } catch (Exception ex) {
            log.error("Error parsing json response - {}", ex.getMessage());
            throw new GsonParsingException(ex.getMessage());
        }
    }

    /**
     * This allows dynamic adapter creating for each class that requires deserialization
     *
     * @param clazz
     * @return
     */
    private Gson initializeViaGsonBuilderForClass(Class clazz) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateJsonDeserializer())
                .registerTypeAdapter(clazz, new GsonRequiredAnnotationDeserializer())
                .create();

        return gson;
    }
}
