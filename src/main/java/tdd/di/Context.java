package tdd.di;

import jakarta.inject.Provider;

import java.util.HashMap;
import java.util.Map;

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

    public <ComponentType> void bind(Class<ComponentType> type, ComponentType instance) {
        providers.put(type, (Provider<ComponentType>) () -> instance);
    }

    public <ComponentType, ComponentImplementation extends ComponentType>
    void bind(Class<ComponentType> type, Class<ComponentImplementation> implementation) {
        componentImplementations.put(type, implementation);
        providers.put(type, (Provider<ComponentType>) () -> {
            try {
                return (ComponentType) ((Class<?>) implementation).getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <ComponentType> ComponentType get(Class<ComponentType> type) {
        return (ComponentType) providers.get(type).get();
    }

}
