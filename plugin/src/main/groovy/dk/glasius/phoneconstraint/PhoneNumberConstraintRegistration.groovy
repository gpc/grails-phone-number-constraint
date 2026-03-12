package dk.glasius.phoneconstraint

import groovy.util.logging.Slf4j
import org.grails.datastore.gorm.validation.constraints.eval.ConstraintsEvaluator
import org.grails.datastore.gorm.validation.constraints.eval.DefaultConstraintEvaluator
import org.grails.datastore.gorm.validation.constraints.registry.ConstraintRegistry
import org.grails.datastore.gorm.validation.constraints.registry.DefaultValidatorRegistry
import org.grails.datastore.mapping.validation.ValidatorRegistry
import org.springframework.context.ApplicationContext

@Slf4j
class PhoneNumberConstraintRegistration {
    static void register(ApplicationContext applicationContext) {
        registerPhoneNumberConstraintOnBeans(applicationContext, ConstraintsEvaluator, DefaultConstraintEvaluator) {
            it.constraintRegistry
        }
        registerPhoneNumberConstraintOnBeans(applicationContext, ValidatorRegistry, DefaultValidatorRegistry)
        registerPhoneNumberConstraintOnBeans(applicationContext, ConstraintRegistry, ConstraintRegistry)
    }

    private static void registerPhoneNumberConstraintOnBeans(ApplicationContext applicationContext,
                                                             Class interfaceClass,
                                                             Class clazz,
                                                             Closure<ConstraintRegistry> closure = Closure.IDENTITY) {
        Map<String, ?> evaluators = applicationContext.getBeansOfType(interfaceClass)
        evaluators.each { name, evaluator ->
            if (clazz.isAssignableFrom(evaluator.getClass())) {
                ConstraintRegistry reg = closure.call(evaluator)
                reg.addConstraint(PhoneNumberConstraint)
            }
            log.debug("Registered PhoneNumberConstraint on $name evaluator on $interfaceClass")
        }
    }
}
