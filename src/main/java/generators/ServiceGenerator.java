package generators;

import io.swagger.models.Operation;
import io.swagger.models.Path;

import java.util.Map;

public class ServiceGenerator {

    public static String generate(String title, Map<String, Path> paths) {
        StringBuilder builder = new StringBuilder();
        builder.append(generateHeader(title));
        paths.forEach((String name, Path path) -> path.getOperations()
                .forEach((Operation o) -> builder.append(generateMethod(o.getOperationId()))));
        builder.append(generateFooter());
        return builder.toString();
    }

    private static String generateFooter() {
        return "}\n";
    }

    private static String generateMethod(String operationId) {
        return String.format("    %sResponse %s(1: %sRequest request)\n",
                             operationId,
                             operationId,
                             operationId);
    }

    private static String generateHeader(String title) {
        return String.format("service %sService {\n", title);
    }
}
