package util;

import io.swagger.models.ArrayModel;
import io.swagger.models.RefModel;
import io.swagger.models.parameters.AbstractSerializableParameter;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.SerializableParameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;

import java.util.HashMap;
import java.util.Map;

public class FrugalTypeResolver {

    private static Map<String, String> INTEGER_MAP = new HashMap<String, String>() {{
        put("int32", "i32");
        put("int64", "i64");
    }};

    public static String resolve(String name,
                                 Property property) throws InvalidTypeException {
        if (property instanceof RefProperty) {
            // Return early if this is a ref. Hopefully we'll generate this struct.
            return ((RefProperty) property).getSimpleRef();
        } else if (property instanceof MapProperty) {
            Property internalProperty = ((MapProperty) property).getAdditionalProperties();
            // The internal property shouldn't have a name so just pass null
            return String.format("map<string,%s>", resolve(null, internalProperty));
        }


        String type = property.getType();
        switch (type) {
            case "integer":
                String format = property.getFormat();
                return format == null ? INTEGER_MAP.get("int32") : INTEGER_MAP.get(
                        format);
            case "number":
                return "double";
            case "string":
                return "string";
            case "boolean":
                return "bool";
            case "array":
                return resolveArray(((ArrayProperty) property).getItems());
            case "object":
                return name;
            default:
                throw new InvalidTypeException(String.format(
                        "Type `%s` is not a valid swagger type.",
                        type));
        }
    }

    public static String resolve(String name, Parameter parameter) {

        if (parameter instanceof BodyParameter) {
            BodyParameter p = (BodyParameter) parameter;
            if (p.getSchema() instanceof RefModel) {
                return ((RefModel) p.getSchema()).getSimpleRef();
            }
            if (p.getSchema() instanceof ArrayModel) {
                return resolveArray(((ArrayModel) p.getSchema()).getItems());
            }
        }

        AbstractSerializableParameter p = (AbstractSerializableParameter) parameter;
        String type = p.getType();

        switch (type) {
            case "integer":
                String format = p.getFormat();
                return format == null ? INTEGER_MAP.get("int32") : INTEGER_MAP.get(
                        format);
            case "number":
                return "double";
            case "string":
                return "string";
            case "boolean":
                return "bool";
            case "array":
                return resolveArray(p.getItems());
            case "object":
                return name;
            default:
                throw new InvalidTypeException(String.format(
                        "Type `%s` is not a valid swagger type.",
                        type));
        }

    }

    private static String resolveArray(Property items) {
        // TODO Check for `uniqueItems: true` and use a set
        // XXX: I'm pretty sure passing "items" here is a bug but I don't have
        // tests to confirm.
        return String.format("list<%s>", resolve("items", items));
    }
}

class InvalidTypeException extends RuntimeException {
    InvalidTypeException(String message) {
        super(message);
    }
}
