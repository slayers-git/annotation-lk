package ru.bgpu.annotationlk;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.Scanners;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppConfigWorker {

    private static Logger logger = Logger.getLogger(AppConfigWorker.class.getName());

    public static void configProcessing(String prefix, String filePropName) {

        Reflections reflections = new Reflections(prefix, Scanners.FieldsAnnotated);

        File prop = new File(filePropName);
        if(prop.isFile()) {
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(prop));

                reflections.getFieldsAnnotatedWith(AppConfig.class).forEach(
                        field -> {

                            String value = properties.getProperty(
                                    field.getName(),
                                    field.getAnnotation(AppConfig.class).defValue()
                            );
                            Object targetValue = null;
                            
                            Class<?> type = field.getType ();
                            if (type.isArray ())
                            	type = type.getComponentType();

                            if(type.equals(String.class)) {
                                targetValue = value;
                            } else if(type.equals(Integer.class)) {
                                targetValue = Integer.valueOf(value);
                            } else if (type.equals (Float.class)) {
                            	targetValue = Float.valueOf(value);
                            } else if (type.equals (Double.class)) {
                            	targetValue = Double.valueOf(value);
                            } else if (type.equals (float.class)) {
                            	targetValue = Float.parseFloat(value);
                            } else if (type.equals (double.class)) {
                            	targetValue = Double.parseDouble(value);
                            } else if (type.equals (int.class)) {
                            	targetValue = Integer.parseInt(value);
                            } else return;
                            
                            try {
                                field.setAccessible(true);
                                if (field.getType ().isArray()) {
                                	Object arr = Array.newInstance(type, 1);
                                	Array.set(arr, 0, targetValue);
                                	
                                	field.set(field.getDeclaringClass(), arr);
                                } else {
                                	field.set(field.getDeclaringClass(), targetValue);
                                }
                                field.setAccessible(false);
                            } catch (IllegalAccessException e) {
                                logger.log(
                                        Level.WARNING,
                                        "error set "+field.getDeclaringClass().getName()
                                                +"."+field.getName()+" "+value
                                );
                            }

//                            System.out.println(field.getName());
                        }
                );
            } catch (Exception e) {
                logger.log(Level.WARNING, "error load properties", e);
            }
        } else {
            logger.log(Level.WARNING, "config file not found");
        }
    }

}
