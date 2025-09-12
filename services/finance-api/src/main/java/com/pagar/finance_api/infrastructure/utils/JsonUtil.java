package com.pagar.finance_api.infrastructure.utils;

import com.pagar.finance_api.infrastructure.exceptions.JsonValidationException;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {

    private JsonUtil(){}

    public static boolean hasNullFields(Object obj) {
        return !extractNullFields(obj).isEmpty();
    }

    public static String getNullFieldsAsString(Object obj) {
        return String.join(", ", extractNullFields(obj));
    }

    private static List<String> extractNullFields(Object obj) {
        List<String> nullFields = new ArrayList<>();

        if (obj == null) {
            nullFields.add("entire object is null");
            return nullFields;
        }

        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            ReflectionUtils.makeAccessible(field);
            try {
                Object value = field.get(obj);
                if (value == null) {
                    nullFields.add(field.getName());
                }
            } catch (IllegalAccessException e) {
                throw new JsonValidationException("Error accessing field: " + field.getName(), e);
            }
        }
        return nullFields;
    }
}
