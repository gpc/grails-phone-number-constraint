package dk.glasius.phoneconstraint

import grails.validation.ValidationErrors
import spock.lang.Specification
import spock.lang.Unroll

import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat

@Unroll
class PhoneNumberConstraintSpec extends Specification {

    PhoneNumberConstraint constraint    
    ValidationErrors errors = Mock() 

    void setup() {
    }

    void "constraint name and config should set constraints config correct"() {
        when:
        constraint = new PhoneNumberConstraint(
                ValidateableProperty,
                'property',
                cfg,
                null
        )

        then:
        verifyAll(constraint) {
            name == 'phoneNumber'
            enabled == expectEnabled
            defaultRegion == expectedRegion
            phoneNumberFormat == expectedFormat
        }

        where:
        cfg                                      || expectEnabled | expectedRegion | expectedFormat
        false                                    || false         | 'US'           | PhoneNumberFormat.INTERNATIONAL
        true                                     || true          | 'US'           | PhoneNumberFormat.INTERNATIONAL
        'DK'                                     || true          | 'DK'           | PhoneNumberFormat.INTERNATIONAL
        [region: 'GB', numberFormat: 'NATIONAL'] || true          | 'GB'           | PhoneNumberFormat.NATIONAL
        [region: 'GB']                           || true          | 'GB'           | PhoneNumberFormat.INTERNATIONAL
        [numberFormat: 'RFC3966']                || true          | 'US'           | PhoneNumberFormat.RFC3966
        [numberFormat: 'E164']                   || true          | 'US'           | PhoneNumberFormat.E164
    }

    void "constraint config with errors"() {
        
    }
    
}
