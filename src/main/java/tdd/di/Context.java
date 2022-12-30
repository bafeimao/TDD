package tdd.di;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @projectName: TDD
 * @package: PACKAGE_NAME
 * @className: tdd.di.Context
 * @author: ycd20
 * @description: context
 * @date: 2022/12/29 14:01
 * @version: 1.0
 */
public class Context {

    private Map<Class<?>, Provider<?>> providers = new HashMap<>();

    public <Type> void bind(Class<Type> type, Type instance) {
        providers.put(type, (Provider<Type>) () -> instance);
    }

    public <Type, Implementation extends Type> void bind(Class<Type> type, Class<Implementation> implementation) {
        Constructor<Implementation> injectionConstructor = getInjectConstructor(implementation);
        providers.put(type, (Provider<Type>) () -> {
            try {
                Object[] dependencies = Arrays.stream(injectionConstructor.getParameters()).map(parameter -> get(parameter.getType()).orElseThrow(DependencyNotFoundException::new)).toArray(Object[]::new);
                return (Type) injectionConstructor.newInstance(dependencies);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static <Type> Constructor<Type> getInjectConstructor(Class<Type> implementation) {
        List<Constructor<?>> injectsConstructors = Arrays.stream(implementation.getConstructors()).filter(c -> c.isAnnotationPresent(Inject.class)).toList();
        if (injectsConstructors.size() > 1) {
            throw new IllegalComponentException();
        }

        return (Constructor<Type>) injectsConstructors.stream().findFirst().orElseGet(() -> {
            try {
                return implementation.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalComponentException();
            }
        });
    }

    public <Type> Optional<Type> get(Class<Type> type) {
        return Optional.ofNullable(providers.get(type)).map(p -> (Type) p.get());
    }
}
