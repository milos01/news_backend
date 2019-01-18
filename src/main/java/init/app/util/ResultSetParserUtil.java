package init.app.util;

import org.modelmapper.ModelMapper;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Enum.valueOf;

public class ResultSetParserUtil {

    public static List<Object> parseAll(List<Object[]> resultSet, Class clazz) {

        List<Object> objects = new ArrayList<>();

        for (Object[] object : resultSet) {
            objects.add(parseSingleArray(object, clazz));
        }

        return objects;
    }

    public static Object parseSingleArray(Object[] result, Class clazz) {
        if (result == null) {
            return null;
        }else if(result.getClass().isArray()){
            if(result.length == 0){
                return null;
            }
            if(result[0]!=null && result[0].getClass().isArray()){
                result = (Object[])result[0];
            }
        }

        Object newObject = null;

        try {
            // TODO: 1/30/18 ispraviti convert reda u listu ili dodavanje reda na red (bolja opcija)
            List<Field> fields = clazz.getSuperclass() != null ? Arrays.stream(clazz.getSuperclass().getDeclaredFields()).filter(x-> !Objects.equals(x.getName(), "serialVersionUID")).collect(Collectors.toList()) : new ArrayList<>();
            fields.addAll(Arrays.stream(clazz.getDeclaredFields()).filter(x-> !Objects.equals(x.getName(), "serialVersionUID")).collect(Collectors.toList()));
            newObject = clazz.newInstance();

            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                field.setAccessible(true);

                final Class fieldType = field.getType();

                if (result.length <= i || result[i] == null) {
                    field.set(newObject, null);
                } else if(fieldType == Boolean.class && result[i].getClass() == Integer.class) {
                    field.set(newObject, (Integer)result[i] == 1);
                } else if(fieldType == Boolean.class && result[i].getClass() == BigInteger.class) {
                    field.set(newObject, ((BigInteger)result[i]).intValue() == 1);
                } else if (result[i].getClass() == BigInteger.class) {
                    field.set(newObject, ((BigInteger) result[i]).longValue());
                } else if (result[i].getClass() == Timestamp.class) {
                    field.set(newObject, ((Timestamp) result[i]).toInstant().atZone(TimeZone.getDefault().toZoneId()));
                } else if (fieldType.isEnum()) {
                    field.set(newObject, valueOf(fieldType, (String) result[i]));
                } else {
                    field.set(newObject, fieldType.cast(result[i]));
                }
            }
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        return newObject;
    }

    public static Object parseSingleObject(Object result, Class clazz) {

        ModelMapper modelMapper = new ModelMapper();

        try {

            Object newObject = clazz.newInstance();
            newObject = modelMapper.map(result, newObject.getClass());
            return newObject;


        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<Object> parseListOfObjects(List<Object> resultSet, Class clazz) {
        List<Object> objects = new ArrayList<>();

        for (Object object : resultSet) {
            objects.add(parseSingleObject(object, clazz));
        }

        return objects;
    }
}
