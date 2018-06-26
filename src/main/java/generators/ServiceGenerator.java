package generators;

import io.swagger.models.HttpMethod;
import io.swagger.models.Operation;
import io.swagger.models.Path;

import java.util.Map;

public class ServiceGenerator {

    public static String generate(String title, Map<String, Path> paths) {
        StringBuilder builder = new StringBuilder();
        builder.append(generateHeader(title));
        paths.forEach((String name, Path path) -> path.getOperationMap()
                .forEach((HttpMethod method, Operation o) -> builder.append(generateMethod(o.getOperationId(), name, method.name()))));
        builder.append(generateFooter());
        return builder.toString();
    }

    private static String generateFooter() {
        return "}\n";
    }

    private static String generateMethod(String operationId, String path, String method) {
        return String.format("    %sResponse %s(1: %sRequest request) (http.pathTemplate=\"%s\") (http.method=\"%s\")\n",
                             capitalize(operationId),
                             operationId,
                             capitalize(operationId),
                             path,
                             method);
    }

    private static String generateHeader(String title) {
        return String.format("service %sService {\n", title);
    }

    private static String capitalize(String in) {
        return in.substring(0, 1).toUpperCase() + in.substring(1);
    }
}
