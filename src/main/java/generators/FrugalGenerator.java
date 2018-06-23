package generators;

import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Swagger;
import io.swagger.models.Path;
import io.swagger.models.properties.Property;
import io.swagger.parser.SwaggerParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Map;

public class FrugalGenerator {
    private static Logger logger = LoggerFactory.getLogger(FrugalGenerator.class);

    public static void generate() {

        Swagger swagger = new SwaggerParser().read(
                "./swagger.json");
        Map<String, Model> definitions = swagger.getDefinitions();
        Map<String, io.swagger.models.Path> paths = swagger.getPaths();

        java.nio.file.Path filePath = Paths.get("./example.frugal");
        OpenOption[] options = {
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE
        };
        try (Writer writer =
                     Files.newBufferedWriter(
                             filePath,
                             Charset.forName("UTF-8"),
                             options)) {
            for (String k : definitions.keySet()) {
                Model m = definitions.get(k);
                String frugal = StructGenerator.generate(k, m);
                writer.write(frugal);
            }
            writer.write(generateStructsForPaths(paths));

        } catch (Exception e) {
            logger.error("whoops", e);
        }
    }

    private static String generateStructsForPaths(Map<String, Path> paths) {
        StringBuilder builder = new StringBuilder();
        for (Path p : paths.values()) {
            for (Operation o : p.getOperations()) {
                String requestName = String.format("%sRequest", o.getOperationId());
                builder.append(StructGenerator.generate(requestName, o));
            }
        }
        return builder.toString();
    }

}
