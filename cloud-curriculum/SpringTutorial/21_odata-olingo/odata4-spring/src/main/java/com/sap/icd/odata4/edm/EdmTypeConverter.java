package com.sap.icd.odata4.edm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.core.edm.primitivetype.SingletonPrimitiveType;

public class EdmTypeConverter {
    private Map<Class<?>, EdmPrimitiveTypeKind> edmPrimitiveTypeMap = new HashMap<>();

    public EdmTypeConverter() {
        edmPrimitiveTypeMap.put(Integer.class, EdmPrimitiveTypeKind.Int32);
        edmPrimitiveTypeMap.put(Long.class, EdmPrimitiveTypeKind.Int64);
        edmPrimitiveTypeMap.put(Date.class, EdmPrimitiveTypeKind.Date);
        edmPrimitiveTypeMap.put(String.class, EdmPrimitiveTypeKind.String);
        edmPrimitiveTypeMap.put(Object.class, EdmPrimitiveTypeKind.String);
    }

    public EdmPrimitiveTypeKind getEdmPrimitiveTypeFor(Class<?> clazz) {
        if (edmPrimitiveTypeMap.containsKey(clazz)) {
            return edmPrimitiveTypeMap.get(clazz);
        }
        for (Class<?> c : edmPrimitiveTypeMap.keySet()) {
            if (c.isAssignableFrom(clazz)) {
                edmPrimitiveTypeMap.put(clazz, edmPrimitiveTypeMap.get(c));
                break;
            }
        }
        return edmPrimitiveTypeMap.get(clazz);
    }

    @SuppressWarnings("deprecation")
    public static Object deserialize(SingletonPrimitiveType edmKeyPropertyType,
            String value) {
        if (edmKeyPropertyType.getName().equals("Int32"))
            return new Integer(value);
        if (edmKeyPropertyType.getName().equals("Int64"))
            return new Long(value);
        if (edmKeyPropertyType.getName().equals("Date"))
            return Date.parse(value);
        if (edmKeyPropertyType.getName().equals("String"))
            return value;
        return null;
    }
}
