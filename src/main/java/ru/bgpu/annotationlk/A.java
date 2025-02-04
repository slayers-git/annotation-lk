package ru.bgpu.annotationlk;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

public class A {

    @AppConfig
    static private String host;

    @AppConfig
    static public Integer port;

    @AppConfig(defValue="my-name")
    static private String serverName;
    
    @AppConfig
    static private Double doubletrouble;
    
    @AppConfig
    static private float floating;
    
    @AppConfig
    static private int integration;
    
    @AppConfig
    static private int[] intarray;
    
    @AppConfig
    static private String[] stringarr;

    @Override
    public String toString() {
    	String result = "A = {\n";
        Reflections reflections = new Reflections("ru.bgpu.annotationlk", Scanners.FieldsAnnotated);
        Set<Field> fields = reflections.getFieldsAnnotatedWith(AppConfig.class);
        for (Field field : fields) {
        	try {
        		Object obj = field.get(field.getDeclaringClass());
        		result += "\t" + field.getName() + " = ";
        		if (obj.getClass().isArray()) {
        			result += "{ ";
        			for (int i = 0; i < Array.getLength(obj); ++i) {
        				result += Array.get(obj, i);
        			}
        			result += " }";
        		} else {
        			result += obj;
        		}
        		result +=  ",\n";
        	} catch (IllegalAccessException e) {
        		e.printStackTrace();
            }
        }
        result += "}";

        return result;
//        return "A{" +
//                "host='" + host + '\'' +
//                ", port=" + port +
//                ", name=" + serverName +
//                '}';
    }
}
