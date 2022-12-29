package tdd.di;

import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @projectName: TDD
 * @package: PACKAGE_NAME
 * @className: tdd.di.ContainerTest
 * @author: ycd20
 * @description: TODO
 * @date: 2022/12/29 13:50
 * @version: 1.0
 */
public class ContainerTest {

    Context context;

    @BeforeEach
    public void setup() {
        context = new Context();
    }

    @Nested
    public class ComponentConstruction {
        //todo instance
        @Test
        public void should_bind_type_to_a_specific_instance() {
            Component instance = new Component() {
            };
            context.bind(Component.class, instance);

            Assertions.assertSame(instance, context.get(Component.class));
        }
        //todo abstract class
        //todo interface

        @Nested
        public class ConstructedInjection {
            @Test
            public void should_bind_type_to_a_class_with_default_constructor() {
                context.bind(Component.class, ComponentWithDefaultConstruct.class);
                Component instance = context.get(Component.class);

                assertNotNull(instance);
                assertTrue(instance instanceof ComponentWithDefaultConstruct);
            }

            @Test
            public void should_bind_type_to_a_class_with_inject_constructor() {
                Dependency dependency = new Dependency() {
                };
                context.bind(Component.class, ComponentWithInjectConstructor.class);
                context.bind(Dependency.class, dependency);

                Component instance = context.get(Component.class);
                assertNotNull(instance);
                assertSame(dependency, ((ComponentWithInjectConstructor) instance).getDependency());
            }

            @Test
            public void should_bind_type_to_a_class_with_transitive_dependencies() {
                context.bind(Component.class, ComponentWithInjectConstructor.class);
                context.bind(Dependency.class, DependencyWithInjectConstructor.class);
                context.bind(String.class, "indirect dependency");
                Component instance = context.get(Component.class);
                assertNotNull(instance);

                Dependency dependency = ((ComponentWithInjectConstructor) instance).getDependency();
                assertNotNull(dependency);

                assertEquals("indirect dependency", ((DependencyWithInjectConstructor) dependency).getDependency());
            }

            //todo multi inject constructors
            @Test
            public void should_throw_exception_if_multi_inject_constructors_provider() {
                assertThrows(IllegalComponentException.class, () -> {
                    context.bind(Component.class, ComponentWithMultiInjectConstructors.class);
                });
            }

            //todo no default construct and inject constructor
            @Test
            public void should_throw_exception_if_no_inject_nor_default_constructor_provided() {
                assertThrows(IllegalComponentException.class, () -> {
                    context.bind(Component.class, componentWithNoInjectConstructorsNorDefaultConstructor.class);
                });
            }
            //todo dependencies not exist
        }

        @Nested
        public class FieldInjection {

        }

        @Nested
        public class MethodInjection {

        }
    }

    @Nested
    public class DependenciesSelection {

    }

    @Nested
    public class LifeCycleManagement {

    }

}


interface Component {

}

interface Dependency {

}

class ComponentWithDefaultConstruct implements Component {
    public ComponentWithDefaultConstruct() {

    }
}

class ComponentWithInjectConstructor implements Component {
    private Dependency dependency;

    @Inject
    public ComponentWithInjectConstructor(Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency getDependency() {
        return dependency;
    }
}

class ComponentWithMultiInjectConstructors implements Component {
    @Inject
    public ComponentWithMultiInjectConstructors(String name, Double value) {

    }

    @Inject
    public ComponentWithMultiInjectConstructors(String name) {

    }
}

class componentWithNoInjectConstructorsNorDefaultConstructor implements Component {
    public componentWithNoInjectConstructorsNorDefaultConstructor(String name){

    }
}

class DependencyWithInjectConstructor implements Dependency {
    private String dependency;

    @Inject
    public DependencyWithInjectConstructor(String dependency) {
        this.dependency = dependency;
    }

    public String getDependency() {
        return dependency;
    }
}
