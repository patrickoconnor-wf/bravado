package generators;

import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.parameters.*;
import io.swagger.models.properties.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FrugalTypeResolver;

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

    public static String generate(String name, Operation o) {
        StringBuilder builder = new StringBuilder();
        builder.append(generateHeader(name));
        int index = 1;
        for (Parameter p : o.getParameters()) {
//            if (p instanceof BodyParameter && ((BodyParameter) p).getSchema() != null) {
//                // Bail because we are assuming this type was already generated.
//                logger.info("Assuming struct was already generated for `{}`", ((BodyParameter) p).getSchema());
//                return "";
//            }
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

    private static String generateProperty(String key,
                                           Parameter parameter,
                                           int index) {
        return String.format("    %d: %s %s\n",
                             index,
                             FrugalTypeResolver.resolve(key, parameter),
                             key);
    }

    private static String generateHeader(String name) {
        return String.format("struct %s {\n", name);
    }
}
