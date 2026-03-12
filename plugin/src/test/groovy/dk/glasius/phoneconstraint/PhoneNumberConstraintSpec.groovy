package dk.glasius.phoneconstraint

import grails.validation.ValidationErrors
import org.grails.testing.GrailsUnitTest
import org.springframework.context.support.StaticMessageSource
import org.springframework.validation.FieldError
import spock.lang.Specification
import spock.lang.Unroll

import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat

@Unroll
class PhoneNumberConstraintSpec extends Specification implements GrailsUnitTest {

    PhoneNumberConstraint constraint

    void "constraint name and config should set constraints config correct"() {
        when:
        constraint = new PhoneNumberConstraint(
                ValidateablePhoneNumber,
                'property',
                cfg,
                null
        )

        then:
        verifyAll(constraint) {
            name == 'phoneNumber'
            enabled == expectEnabled
            defaultRegion == expectedRegion
            phoneNumberFormat == expectedNumberFormat
            doFormatting == expectedDoFormatting
        }

        where:
        cfg                                      || expectEnabled | expectedRegion | expectedNumberFormat            | expectedDoFormatting
        false                                    || false         | 'US'           | PhoneNumberFormat.INTERNATIONAL | false
        true                                     || true          | 'US'           | PhoneNumberFormat.INTERNATIONAL | false
        'DK'                                     || true          | 'DK'           | PhoneNumberFormat.INTERNATIONAL | false
        [region: 'GB', numberFormat: 'NATIONAL'] || true          | 'GB'           | PhoneNumberFormat.NATIONAL      | false
        [region: 'GB', format: true]             || true          | 'GB'           | PhoneNumberFormat.INTERNATIONAL | true
        [region: 'GB']                           || true          | 'GB'           | PhoneNumberFormat.INTERNATIONAL | false
        [numberFormat: 'RFC3966']                || true          | 'US'           | PhoneNumberFormat.RFC3966       | false
        [numberFormat: 'E164']                   || true          | 'US'           | PhoneNumberFormat.E164          | false
        [format: true]                           || true          | 'US'           | PhoneNumberFormat.INTERNATIONAL | true
    }

    void "constraint config with errors"() {
        when:
        constraint = new PhoneNumberConstraint(
                ValidateablePhoneNumber,
                'property',
                cfg,
                null
        )
        then:
        IllegalArgumentException t = thrown()
        t.message == expectedMessage

        where:
        cfg                                                     || expectedMessage
        'DX'                                                    || 'Wrong [region] string. [DX] is not supported'
        [region: 'GB', numberFormat: 'NATION']                  || 'Wrong config parameter [numberFormat]. Must be one of [E164, INTERNATIONAL, NATIONAL, RFC3966], but was [NATION]'
        [region: 'GB', numberFormat: 'NATIONAL', format: 'foo'] || 'Wrong config parameter [format]. [foo] is not a boolean'
        [region: 'GX']                                          || 'Wrong config parameter [region]. [GX] is not supported'
        [numberFormat: 'RFC3988']                               || 'Wrong config parameter [numberFormat]. Must be one of [E164, INTERNATIONAL, NATIONAL, RFC3966], but was [RFC3988]'
        [numberFormat: 'E112']                                  || 'Wrong config parameter [numberFormat]. Must be one of [E164, INTERNATIONAL, NATIONAL, RFC3966], but was [E112]'
        [format: 'foo']                                         || 'Wrong config parameter [format]. [foo] is not a boolean'
    }

    void "validate phoneNumber on bean with no errors"() {
        given:
        constraint = new PhoneNumberConstraint(
                ValidateablePhoneNumber,
                'phoneNumber',
                cfg,
                null
        )
        ValidateablePhoneNumber target = new ValidateablePhoneNumber(number as String)
        ValidationErrors errors = Mock()

        when:
        constraint.validate(target, number, errors)

        then:
        0 * errors.addError(_)

        where:
        [cfg, number] << [
                [true, 'DK', [region: 'DK', numberFormat: 'NATIONAL']],
                ['+4540404040', '+1 510 874 4567', '+15108744567', '+81 80-1234-5678', '+818012345678', '+61 4 1234 5678', '+61412345678', '+86 139 1099 8888', '+8613910998888']
        ].combinations()
    }

    void "validate phoneNumber on bean with errors"() {
        given: 'A default message in messageSource'
        (messageSource as StaticMessageSource).addMessage('default.invalid.phoneNumber.message', Locale.default, 'Property [{0}] of class [{1}] with value [{2}] does not pass phone-number validation')

        and: 'a constraint'
        constraint = new PhoneNumberConstraint(
                ValidateablePhoneNumber,
                'phoneNumber',
                cfg,
                messageSource
        )
        and: 'data to validate'
        ValidateablePhoneNumber target = new ValidateablePhoneNumber(number as String)

        and: 'an errors object to add errors to'
        ValidationErrors errors = Spy(new ValidationErrors(target))

        when:
        constraint.validate(target, number, errors)

        then:
        1 * errors.addError(_) >> { FieldError err ->
            verifyAll(err) {
                code == 'phoneNumber.invalid'
                field == 'phoneNumber'
                codes.size() == 20
                rejectedValue == number
                defaultMessage == 'Property [{0}] of class [{1}] with value [{2}] does not pass phone-number validation'
            }
        }

        where:
        [cfg, number] << [
                [true, 'DK', [region: 'DK', numberFormat: 'NATIONAL']],
                ['+4544040', '+1 5 4567', '+151087', '+8078', '12345678', '1234 5678', '+6412345678', '+869 1099 8888', '+888']
        ].combinations()
    }

    void "format phoneNumber"() {
        given:
        constraint = new PhoneNumberConstraint(
                ValidateablePhoneNumber,
                'phoneNumber',
                cfg,
                null
        )
        ValidateablePhoneNumber target = new ValidateablePhoneNumber(number as String)
        ValidationErrors errors = Mock()

        when:
        constraint.validate(target, number, errors)

        then:
        0 * errors.addError(_)

        and:
        target.phoneNumber == expectedNumber

        where:
        cfg                                                         | number        || expectedNumber
        [region: 'DK', numberFormat: 'NATIONAL', format: true]      | '40506070'     | '40 50 60 70'
        [region: 'DK', numberFormat: 'NATIONAL', format: true]      | '+4540506070'  | '40 50 60 70'
        [region: 'DK', format: true]                                | '40506070'     | '+45 40 50 60 70'
        [region: 'DK', format: true]                                | '+4540506070'  | '+45 40 50 60 70'
        [region: 'US', numberFormat: 'NATIONAL', format: true]      | '+4540506070'  | '40 50 60 70'
        [region: 'US', numberFormat: 'NATIONAL', format: true]      | '+15105551234' | '(510) 555-1234'
        [region: 'US', numberFormat: 'INTERNATIONAL', format: true] | '+15105551234' | '+1 510-555-1234'
        [region: 'US', numberFormat: 'RFC3966', format: true]       | '+15105551234' | 'tel:+1-510-555-1234'
        [region: 'US', numberFormat: 'E164', format: true]          | '+15105551234' | '+15105551234'
    }
}
