package com.bloxico.legacy.util.gson;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

@Component
@Slf4j
public class GsonRequiredAnnotationDeserializer implements JsonDeserializer {

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Object pojo = new Gson().fromJson(json, typeOfT);

        Field[] fields = pojo.getClass().getDeclaredFields();

        // Getting all fields of the class and checking if all required ones were provided.
        checkRequiredFields(fields, pojo);

        // Checking if all required fields of parent classes were provided.
        checkSuperClasses(pojo);

        // All checks are ok.
        return pojo;
    }


    private void checkRequiredFields(Field[] declaredFields, Object pojo) {

        //recursively check fields
        if (pojo instanceof List) {
            final List pojoList = (List) pojo;
            for (final Object pojoListPojo : pojoList) {
                checkRequiredFields(pojoListPojo.getClass().getDeclaredFields(), pojoListPojo);
                checkSuperClasses(pojoListPojo);
            }
        }

        for (Field f : declaredFields) {
            // If some field has required annotation.
            if (f.getAnnotation(GsonRequired.class) != null) {
                try {
                    // Trying to read this field's value and check that it truly has value.
                    f.setAccessible(true);
                    Object fieldObject = f.get(pojo);
                    if (fieldObject == null) {
                        // Required value is null - throwing error.
                        throw new JsonParseException(String.format("%1$s -> %v0.1.1$s",
                                pojo.getClass().getSimpleName(),
                                f.getName()));
                    } else {
                        checkRequiredFields(fieldObject.getClass().getDeclaredFields(), fieldObject);
                        checkSuperClasses(fieldObject);
                    }
                }

                // Exceptions while reflection.
                catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new JsonParseException(e);
                }
            }
        }
    }

    /**
     * Checks whether all super classes have all required fields.
     *
     * @param pojo Object to check required fields in its superclasses.
     *
     * @throws JsonParseException When some required field was not met.
     * */
    private void checkSuperClasses(Object pojo) throws JsonParseException {
        Class<?> superclass = pojo.getClass();
        while ((superclass = superclass.getSuperclass()) != null) {
            checkRequiredFields(superclass.getDeclaredFields(), pojo);
        }
    }
}
