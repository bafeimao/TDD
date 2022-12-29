package tdd.di;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @projectName: TDD
 * @package: PACKAGE_NAME
 * @className: tdd.di.Context
 * @author: ycd20
 * @description: TODO
 * @date: 2022/12/29 14:01
 * @version: 1.0
 */
public class Context {

    private Map<Class<?>, Object> components = new HashMap<>();
    private Map<Class<?>, Class<?>> componentImplementations = new HashMap<>();

    private Map<Class<?>, Provider<?>> providers = new HashMap<>();

    public <Type> void bind(Class<Type> type, Type instance) {
        providers.put(type, (Provider<Type>) () -> instance);
    }

    public <Type, Implementation extends Type> void bind(Class<Type> type, Class<Implementation> implementation) {
        Constructor<?>[] injectConstructors = Arrays.stream(implementation.getConstructors()).filter(c -> c.isAnnotationPresent(Inject.class)).toArray(Constructor<?>[]::new);
        if (injectConstructors.length > 1) {
            throw new IllegalComponentException();
        }
        if (injectConstructors.length == 0 && Arrays.stream(implementation.getConstructors()).filter(
                c -> c.getParameters().length == 0
        ).findFirst().map(c -> false).orElse(true)) {
            throw new IllegalComponentException();
        }
        componentImplementations.put(type, implementation);
        providers.put(type, (Provider<Type>) () -> {
            try {
                Constructor<Implementation> injectionConstructor = getConstructor(implementation);
                Object[] dependencies = Arrays.stream(injectionConstructor.getParameters()).map(parameter -> get(parameter.getType())).toArray(Object[]::new);
                return (Type) injectionConstructor.newInstance(dependencies);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static <Type> Constructor<Type> getConstructor(Class<Type> implementation) throws NoSuchMethodException {
        Stream<Constructor<?>> injectsConstructors = Arrays.stream(implementation.getConstructors()).filter(c -> c.isAnnotationPresent(Inject.class));
        return (Constructor<Type>) injectsConstructors.findFirst().orElseGet(() -> {
            try {
                return implementation.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <Type> Type get(Class<Type> type) {
        return (Type) providers.get(type).get();
    }

}
