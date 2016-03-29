package link.mcseu.gubok.api;

import org.jboss.forge.roaster.model.source.JavaClassSource;

public interface Parser {
    public void parse(JavaClassSource source);
}