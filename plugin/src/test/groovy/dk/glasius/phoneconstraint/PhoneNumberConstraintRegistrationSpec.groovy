package dk.glasius.phoneconstraint

import org.grails.datastore.gorm.validation.constraints.eval.ConstraintsEvaluator
import org.grails.datastore.gorm.validation.constraints.eval.DefaultConstraintEvaluator
import org.grails.datastore.gorm.validation.constraints.registry.ConstraintRegistry
import org.grails.datastore.gorm.validation.constraints.registry.DefaultConstraintRegistry
import org.grails.datastore.gorm.validation.constraints.registry.DefaultValidatorRegistry
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.validation.ValidatorRegistry
import org.grails.spring.beans.factory.InstanceFactoryBean
import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

class PhoneNumberConstraintRegistrationSpec extends Specification implements GrailsUnitTest {

    DefaultConstraintRegistry anotherConstraintRegistry = Mock()
    DefaultConstraintEvaluator defaultConstraintEvaluator = Spy(new DefaultConstraintEvaluator(anotherConstraintRegistry, Stub(MappingContext), Collections.emptyMap()))
    DefaultValidatorRegistry defaultValidatorRegistry = Mock()
    DefaultConstraintRegistry defaultConstraintRegistry = Mock()

    def "register with multiple beans"() {
        setup:
        defineBeans {
            constraintEvaluator(InstanceFactoryBean, defaultConstraintEvaluator, ConstraintsEvaluator)
            validatorRegistry(InstanceFactoryBean, defaultValidatorRegistry, ValidatorRegistry)
            constraintRegistry(InstanceFactoryBean, defaultConstraintRegistry, ConstraintRegistry)
        }

        when:
        PhoneNumberConstraintRegistration.register(applicationContext)

        then: 'cascading is added to DefaultConstraintEvaluator'
        1 * anotherConstraintRegistry.addConstraint(PhoneNumberConstraint)

        and: 'cascading is added to DefaultValidatorRegistry'
        1 * defaultValidatorRegistry.addConstraint(PhoneNumberConstraint)

        and: 'cascading is added to DefaultConstraintRegistry'
        1 * defaultConstraintRegistry.addConstraint(PhoneNumberConstraint)
    }

    def "register with missing beans"() {
        setup:
        defineBeans {
        }

        when:
        PhoneNumberConstraintRegistration.register(applicationContext)

        then: 'cascading is not added to DefaultConstraintEvaluator'
        0 * anotherConstraintRegistry.addConstraint(PhoneNumberConstraint)

        and: 'cascading is not added to DefaultValidatorRegistry'
        0 * defaultValidatorRegistry.addConstraint(PhoneNumberConstraint)

        and: 'cascading is not added to DefaultConstraintRegistry'
        0 * defaultConstraintRegistry.addConstraint(PhoneNumberConstraint)
    }
}
