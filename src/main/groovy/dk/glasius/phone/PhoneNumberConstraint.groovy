package dk.glasius.phone

import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat

import groovy.transform.CompileStatic
import org.grails.datastore.gorm.validation.constraints.AbstractConstraint
import org.springframework.context.MessageSource
import org.springframework.validation.Errors

@CompileStatic
class PhoneNumberConstraint extends AbstractConstraint {

    static final String PHONE_CONSTRAINT = "phone"

    boolean enabled = true
    String defaultRegion = 'EN'
    PhoneNumberFormat phoneNumberFormat = PhoneNumberFormat.INTERNATIONAL
    
    PhoneNumberConstraint(Class<?> constraintOwningClass, String constraintPropertyName, Object constraintParameter, MessageSource messageSource) {
        super(constraintOwningClass, constraintPropertyName, constraintParameter, messageSource)

        this.enabled = (boolean) constraintParameter
    }

    @Override
    protected Object validateParameter(Object constraintParameter) {
        if (isBoolean(constraintParameter)) {
            return constraintParameter
        }
        if (isLanguage(constraintParameter)) {
            defaultRegion = constraintParameter as String
        }
        if (isMapConfig(constraintParameter)) {
            Map config = constraintParameter as Map<String, String>
            defaultRegion = config.region ?: defaultRegion
            phoneNumberFormat = parseNumberFormat(config.numberFormat) ?: phoneNumberFormat
        }
        throw new IllegalArgumentException("Parameter for constraint [$PHONE_CONSTRAINT] of property [$constraintPropertyName] of class [$constraintOwningClass] must be a boolean, a language string or a config map")
    }

    @Override
    protected void processValidate(Object target, Object propertyValue, Errors errors) {

    }

    @Override
    boolean supports(Class type) {
        CharSequence.isAssignableFrom(type)
    }

    @Override
    String getName() {
        PHONE_CONSTRAINT
    }

    private static boolean isLanguage(constraintParameter) {
        constraintParameter instanceof String && constraintParameter?.size() == 2
    }

    private static boolean isBoolean(constraintParameter) {
        constraintParameter instanceof Boolean
    }

    private static boolean isMapConfig(constraintParameter) {
        if (constraintParameter instanceof Map) {
            Map config = constraintParameter as Map<String, String>
            if(config.region && config.region.size() != 2) {
                throw new IllegalArgumentException("Wrong config parameter [region]. Must be exactly to chars long") 
            }
            if(config.numberFormat && !parseNumberFormat(config.numberFormat)) {
                throw new IllegalArgumentException("Wrong config parameter [numberFormat]. Must be a value from [com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat]")
            }
            return config.region?.size() == 2 && parseNumberFormat(config.numberFormat)
        }
        return false
    }

    static PhoneNumberFormat parseNumberFormat(String format) {
        PhoneNumberFormat.values().find { it.name() == format }
    }
}


