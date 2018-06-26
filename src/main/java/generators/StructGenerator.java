package generators;

import io.swagger.models.HttpMethod;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Response;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FrugalTypeResolver;

import java.util.Map;

public class StructGenerator {

    private static Logger logger = LoggerFactory.getLogger(StructGenerator.class);

    public static String generate(String name, Model m) {
        StringBuilder builder = new StringBuilder();
        builder.append(generateHeader(name));
        int index = 1;
        for (String key : m.getProperties().keySet()) {
            Property prop = m.getProperties().get(key);
            builder.append(generateProperty(key, prop, index));
            index++;
        }
        builder.append(generateFooter());
        return builder.toString();
    }

    public static String generateRequest(Operation o) {
        String requestName = String.format("%sRequest", o.getOperationId());
        return generate(requestName, o);
    }

    public static String generate(String name, Operation o) {
        StringBuilder builder = new StringBuilder();
        builder.append(generateHeader(name));
        int index = 1;
        for (Parameter p : o.getParameters()) {
            builder.append(generateProperty(p.getName(), p, index));
            index++;
        }
        builder.append(generateFooter());
        return builder.toString();
    }

    private static String generateFooter() {
        return "}\n\n";
    }

    private static String generateProperty(String key,
                                           Property prop,
                                           int index) {
        return String.format("    %d: %s %s\n",
                             index,
                             FrugalTypeResolver.resolve(key, prop),
                             key);
    }

    private static String generateKnownProperty(String key,
                                                String prop,
                                                int index) {
        return String.format("    %d: %s %s\n", index, prop, key);
    }

    private static String generateProperty(String key,
                                           Parameter parameter,
                                           int index) {
        return String.format("    %d: %s %s\n",
                             index,
                             FrugalTypeResolver.resolve(key, parameter),
                             key);
    }

    private static String generateHeader(String name) {
        return String.format("struct %s {\n", capitalize(name));
    }

    public static String generateResponse(Operation o, HttpMethod method) {
        StringBuilder builder = new StringBuilder();
        String responseName = String.format("%sResponse", capitalize(o.getOperationId()));
        builder.append(generateHeader(responseName));
        builder.append(generateKnownProperty("code", "i32", 1));
        builder.append(generateKnownProperty("message", "string", 2));
        Map<String, Response> responses = o.getResponses();
        int index = 3;
        // GET requests will have a custom output on success
        if (responses.get("200") != null && method == HttpMethod.GET) {
            Property p = responses.get("200").getSchema();
            String name = p.getName() == null ? "items" : p.getName();
            builder.append(generateProperty(name, p, index));
        } else {
            // Return what was sent
            for (Parameter p : o.getParameters()) {
                builder.append(generateProperty(p.getName(), p, index));
                index++;
            }
        }
        builder.append(generateFooter());
        return builder.toString();
    }

    private static String capitalize(String in) {
        return in.substring(0, 1).toUpperCase() + in.substring(1);
    }
}
