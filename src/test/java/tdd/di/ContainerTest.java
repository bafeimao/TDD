package tdd.di;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    interface Component {

    }

    static class ComponentWithDefaultConstruct implements Component {
        public ComponentWithDefaultConstruct() {

        }
    }

    @Nested
    public class ComponentConstruction {
        //todo instance
        @Test
        public void should_bind_type_to_a_specific_instance() {
            Context context = new Context();

            Component instance = new Component() {
            };
            context.bind(Component.class, instance);

            Assertions.assertSame(instance, context.get(Component.class));
        }
        //todo abstract class
        //todo interface

        @Nested
        public class ConstructedInjection {
            //todo no args construction
            @Test
            public void should_bind_type_to_a_class_with_default_constructor() {
                Context context = new Context();
                context.bind(Component.class, ComponentWithDefaultConstruct.class);
                Component instance = context.get(Component.class);

                assertNotNull(instance);
                assertTrue(instance instanceof ComponentWithDefaultConstruct);
            }
            //todo with dependencies
            //todo a -> b ->c

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
