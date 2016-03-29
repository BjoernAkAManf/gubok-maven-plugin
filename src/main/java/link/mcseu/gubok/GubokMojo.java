package link.mcseu.gubok;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "compile", defaultPhase = LifecyclePhase.GENERATE_SOURCES,
      requiresProject = true)
public class GubokMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;
    private final JavaParser parser = new JavaParser();

    @Override
    public void execute() throws MojoExecutionException {
        final Build build = project.getBuild();
        final Path src = new File(build.getSourceDirectory()).toPath();
        final Path dest = src.resolve(build.getOutputDirectory()).getParent()
                .resolve("generated-sources")
                .resolve("gubok");

        setCompileSourceRoot(dest.toAbsolutePath().toString());

        try {
            parse(src, dest);
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new MojoExecutionException(ex.getLocalizedMessage(), ex);
        }
    }

    protected void setCompileSourceRoot(String path) {
        project.getCompileSourceRoots().clear();
        project.addCompileSourceRoot(path);
    }

    protected void parse(Path src, Path sources) throws IOException {
        final Iterator<Path> it = Files.walk(src)
                .filter(f -> !Files.isDirectory(f))
                .iterator();

        while (it.hasNext()) {
            final Path path = it.next();
            final String folder = src.relativize(path).toString();

            parser.parse(path, sources.resolve(folder));
        }
    }
}