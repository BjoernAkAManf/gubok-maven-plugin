package link.mcseu.gubok;

import com.google.common.base.Preconditions;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ServiceLoader;
import link.mcseu.gubok.api.Parser;
import link.mcseu.gubok.internal.AddInjectParser;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;

public final class JavaParser {
    private final ServiceLoader<Parser> parser
            = ServiceLoader.load(Parser.class);

    public void parse(Path src, Path dest) throws IOException {
        Preconditions.checkArgument(!src.equals(dest), "src = dest");

        final JavaClassSource jcs = read(src);

        if (!Files.exists(dest)) {
            Files.createDirectories(dest.getParent());
            Files.createFile(dest);
        }

        parser.forEach(p -> {
            p.parse(jcs);
        });

        Files.write(dest, jcs.toUnformattedString().getBytes());
    }

    private JavaClassSource read(Path p) throws FileNotFoundException {
        return Roaster.parse(JavaClassSource.class, p.toFile());
    }
}