package dk.glasius.phoneconstraint

import grails.databinding.SimpleDataBinder
import grails.databinding.SimpleMapDataBindingSource
import grails.gorm.validation.ConstrainedProperty
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.validation.constraints.AbstractConstraint
import org.springframework.context.MessageSource
import org.springframework.validation.Errors

import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat

@CompileStatic
class PhoneNumberConstraint extends AbstractConstraint {

    static final String PHONE_CONSTRAINT = "phoneNumber"
    static final String DEFAULT_INVALID_PHONE_NUMBER_MESSAGE_CODE = 'default.invalid.phoneNumber.message'
    static final String DEFAULT_INVALID_PHONE_NUMBER_CONSTRAINT = PHONE_CONSTRAINT

    protected boolean enabled = true
    protected String defaultRegion = 'US'
    protected PhoneNumberFormat phoneNumberFormat = PhoneNumberFormat.INTERNATIONAL
    protected boolean doFormatting = false

    PhoneNumberConstraint(Class<?> constraintOwningClass, String constraintPropertyName, Object constraintParameter, MessageSource messageSource) {
        super(constraintOwningClass, constraintPropertyName, constraintParameter, messageSource)

        enabled = validateParameter(constraintParameter) as Boolean
    }

    @Override
    boolean supports(Class type) { CharSequence.isAssignableFrom(type) }

    @Override
    String getName() { PHONE_CONSTRAINT }

    @Override
    protected Object validateParameter(Object constraintParameter) {
        if (isBoolean(constraintParameter)) {
            return constraintParameter
        } else if (isRegionString(constraintParameter)) {
            defaultRegion = constraintParameter as String
            return true
        } else if (isMapConfig(constraintParameter)) {
            Map config = constraintParameter as Map<String, String>
            defaultRegion = config.region ?: defaultRegion
            phoneNumberFormat = PhoneNumberUtil.parseNumberFormat(config.numberFormat) ?: phoneNumberFormat
            doFormatting = config.format != null ? config.format as Boolean : doFormatting
            return config.region || config.numberFormat || config.format != null
        }
        throw new IllegalArgumentException("Parameter for constraint [$PHONE_CONSTRAINT] of property [$constraintPropertyName] of class [$constraintOwningClass] must be a boolean, a [region] string or a config map with keys [region, numberFormat, format]")
    }

    @Override
    protected void processValidate(Object target, Object propertyValue, Errors errors) {
        if (enabled) {
            if (!PhoneNumberUtil.isValid(propertyValue as String, defaultRegion)) {
                Object[] args = [constraintPropertyName, constraintOwningClass, propertyValue]
                rejectValue(target, errors, DEFAULT_INVALID_PHONE_NUMBER_MESSAGE_CODE,
                        DEFAULT_INVALID_PHONE_NUMBER_CONSTRAINT + ConstrainedProperty.INVALID_SUFFIX, args)
            } else {
                if (doFormatting) {
                    String value = PhoneNumberUtil.format(propertyValue as String, defaultRegion, phoneNumberFormat)
                    SimpleMapDataBindingSource source = new SimpleMapDataBindingSource([(constraintPropertyName): value])
                    new SimpleDataBinder().bind(target, source)
                }
            }

        }
    }

    private static boolean isRegionString(constraintParameter) {
        if (constraintParameter instanceof String) {
            if (!PhoneNumberUtil.isValidRegionCode(constraintParameter as String)) {
                throw new IllegalArgumentException("Wrong [region] string. [$constraintParameter] is not supported")
            }
            return true
        }
        return false
    }

    private static boolean isBoolean(constraintParameter) {
        constraintParameter instanceof Boolean
    }

    private static boolean isMapConfig(constraintParameter) {
        if (constraintParameter instanceof Map) {
            Map config = constraintParameter as Map<String, String>
            if (config.region && !PhoneNumberUtil.isValidRegionCode(config.region)) {
                throw new IllegalArgumentException("Wrong config parameter [region]. [$config.region] is not supported")
            }
            if (config.numberFormat && !PhoneNumberUtil.parseNumberFormat(config.numberFormat)) {
                throw new IllegalArgumentException("Wrong config parameter [numberFormat]. Must be one of [${PhoneNumberFormat.values()*.name().join(', ')}], but was [${config.numberFormat}]")
            }
            if (config.format != null && !(config.format instanceof Boolean)) {
                throw new IllegalArgumentException("Wrong config parameter [format]. [$config.format] is not a boolean")
            }
            return true
        }
        return false
    }
}


