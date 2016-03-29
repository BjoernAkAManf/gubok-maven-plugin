package link.mcseu.gubok.internal;

import com.google.common.base.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import link.mcseu.gubok.api.Parser;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

public abstract class AbstractParser implements Parser {
    private static final String CANCLED = "MSG_CANCLED"; 
    private static final Dummy DUMMY = create(Dummy.class, new DummyHandler());

    @Override
    public final void parse(JavaClassSource source) {
        try {
            parse(source, CANCLED);
        } catch(RuntimeException ex) {
            // Allow to skip if we do not match requirements
            if(!CANCLED.equals(ex.getMessage())) {
                throw ex;
            }
        }
    }

    protected static AnnotationSource<JavaClassSource> getAnnotation(
            JavaClassSource src, Class<? extends Annotation> type) {
        final AnnotationSource<JavaClassSource> value = src.getAnnotation(type);
        return value == null ? DUMMY : value;
    }

    protected static AnnotationSource<JavaClassSource> hasAnnotation(
            JavaClassSource src, Class<? extends Annotation> type) {
        return Preconditions.checkNotNull(src.getAnnotation(type), CANCLED);
    }

    protected static AnnotationSource<JavaClassSource> hasAnyAnnotation(
            JavaClassSource src, List<Class<? extends Annotation>> targets) {
        final AnnotationSource<JavaClassSource> target = targets.stream()
                .map(t -> getAnnotation(src, t))
                .filter(Objects::nonNull)
                // It does not matter which constructor we choose
                .findAny()
                // We check for null anyway -> fail
                .orElse(null);

        Preconditions.checkNotNull(target, CANCLED);
        return target;
    }

    protected abstract void parse(JavaClassSource src, String cancled);

    private static <T> T create(Class<T> type, InvocationHandler handler) {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final Class<?>[] types = new Class<?>[]{type};

        return type.cast(Proxy.newProxyInstance(cl, types, handler));
    }

    private static interface Dummy extends AnnotationSource<JavaClassSource> {}

    private static class DummyHandler implements InvocationHandler {
        @Override
        public Object invoke(Object o, Method method, Object[] os) throws
                Throwable {
            return null;
        }
    }
}