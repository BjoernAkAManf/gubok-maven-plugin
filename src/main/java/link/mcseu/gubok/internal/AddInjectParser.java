package link.mcseu.gubok.internal;

import java.lang.annotation.Annotation;
import java.util.List;
import link.mcseu.gubok.annotations.AddInject;
import link.mcseu.gubok.api.Parser;
import lombok.RequiredArgsConstructor;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.kohsuke.MetaInfServices;
import static java.util.Arrays.asList;

@MetaInfServices(Parser.class)
public final class AddInjectParser extends AbstractParser {
    private static final List<Class<? extends Annotation>> TARGETS = asList(
            RequiredArgsConstructor.class
    );

    private static final String INJECT = "@__({@com.google.inject.Inject})";

    @Override
    public void parse(JavaClassSource source, String cancled) {
        hasAnnotation(source, AddInject.class);

        hasAnyAnnotation(source, TARGETS)
                .setLiteralValue("onConstructor", INJECT);
    }
}