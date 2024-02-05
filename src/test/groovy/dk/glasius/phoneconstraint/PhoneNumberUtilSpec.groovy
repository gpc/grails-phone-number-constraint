package dk.glasius.phoneconstraint

import spock.lang.Specification
import spock.lang.Unroll

import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;

@Unroll
class PhoneNumberUtilSpec extends Specification {

    def "Check isValid"() {
        when:
        boolean result = PhoneNumberUtil.isValid(number)

        then:
        result == expected

        where:
        number                      || expected
        '510 874 4567'              || true
        '+1 510 874 4567'           || true
        '5108744567'                || true
        '+15108744567'              || true
        '(510) 874-4567'            || true
        '+1 (510) 874-4567'         || true
        '+1 (510) 874-4567;ext=123' || true
        '+1 (510) 874-4567;ext=123' || true
        '+45 4044 1234'             || true // Denmark
        '+45 40 44 12 34'           || true // Denmark
        '+45 40441234'              || true // Denmark
        '+4540441234'               || true // Denmark
        '+81 80-1234-5678'          || true // Japan
        '+81 8012345678'            || true // Japan
        '+61 4 1234 5678'           || true // Australia
        '+61412345678'              || true // Australia
        '+86 139 1099 8888'         || true // China
        '+8613910998888'            || true // China
        '1235;ext=1'                || false
        '+123 (510) 874-4567'       || false
        '+45404412'                 || false // Denmark missing digits
        '+45 404412'                || false // Denmark missing digits
        '+81 80-1234-567'           || false // Japan missing digits
        '+61 4 1234 567'            || false // Australia missing digits
        '+86 139 1099 888'          || false // China missing digits
    }

    def "format"() {
        when:
        String result = PhoneNumberUtil.format(number)

        then:
        result == expected

        where:
        number                      || expected
        '510 874 4567'              || '+1 510-874-4567'
        '+1 510 874 4567'           || '+1 510-874-4567'
        '5108744567'                || '+1 510-874-4567'
        '+15108744567'              || '+1 510-874-4567'
        '(510) 874-4567'            || '+1 510-874-4567'
        '+1 (510) 874-4567'         || '+1 510-874-4567'
        '+1 (510) 874-4567;ext=123' || '+1 510-874-4567 ext. 123'
        '+1 (510) 874-4567;ext=123' || '+1 510-874-4567 ext. 123'
        '+45 4044 1234'             || '+45 40 44 12 34' // Denmark
        '+45 40 44 12 34'           || '+45 40 44 12 34' // Denmark
        '+45 40441234'              || '+45 40 44 12 34' // Denmark
        '+4540441234'               || '+45 40 44 12 34' // Denmark
        '+81 80-1234-5678'          || '+81 80-1234-5678' // Japan
        '+818012345678'             || '+81 80-1234-5678' // Japan
        '+61 4 1234 5678'           || '+61 412 345 678' // Australia
        '+61412345678'              || '+61 412 345 678' // Australia
        '+86 139 1099 8888'         || '+86 139 1099 8888' // China
        '+8613910998888'            || '+86 139 1099 8888' // China
    }

    def "format with english and national"() {
        when:
        String result = PhoneNumberUtil.format(number, 'US', PhoneNumberFormat.NATIONAL)

        then:
        result == expected

        where:
        number                      || expected
        '510 874 4567'              || '(510) 874-4567'
        '+1 510 874 4567'           || '(510) 874-4567'
        '5108744567'                || '(510) 874-4567'
        '+15108744567'              || '(510) 874-4567'
        '(510) 874-4567'            || '(510) 874-4567'
        '+1 (510) 874-4567'         || '(510) 874-4567'
        '+1 (510) 874-4567;ext=123' || '(510) 874-4567 ext. 123'
        '+1 (510) 874-4567;ext=123' || '(510) 874-4567 ext. 123'
        '+45 4044 1234'             || '40 44 12 34' // Denmark
        '+45 40 44 12 34'           || '40 44 12 34' // Denmark
        '+45 40441234'              || '40 44 12 34' // Denmark
        '+4540441234'               || '40 44 12 34' // Denmark
        '+81 80-1234-5678'          || '080-1234-5678' // Japan
        '+818012345678'             || '080-1234-5678' // Japan
        '+61 4 1234 5678'           || '0412 345 678' // Australia
        '+61412345678'              || '0412 345 678' // Australia
        '+86 139 1099 8888'         || '139 1099 8888' // China
        '+8613910998888'            || '139 1099 8888' // China
    }

    def "format with english and RFC3966"() {
        when:
        String result = PhoneNumberUtil.format(number, 'US', PhoneNumberFormat.RFC3966)

        then:
        result == expected

        where:
        number                      || expected
        '510 874 4567'              || 'tel:+1-510-874-4567'
        '+1 510 874 4567'           || 'tel:+1-510-874-4567'
        '5108744567'                || 'tel:+1-510-874-4567'
        '+15108744567'              || 'tel:+1-510-874-4567'
        '(510) 874-4567'            || 'tel:+1-510-874-4567'
        '+1 (510) 874-4567'         || 'tel:+1-510-874-4567'
        '+1 (510) 874-4567;ext=123' || 'tel:+1-510-874-4567;ext=123'
        '+1 (510) 874-4567;ext=123' || 'tel:+1-510-874-4567;ext=123'
        '+45 4044 1234'             || 'tel:+45-40-44-12-34' // Denmark
        '+45 40 44 12 34'           || 'tel:+45-40-44-12-34' // Denmark
        '+45 40441234'              || 'tel:+45-40-44-12-34' // Denmark
        '+4540441234'               || 'tel:+45-40-44-12-34' // Denmark
        '+81 80-1234-5678'          || 'tel:+81-80-1234-5678' // Japan
        '+818012345678'             || 'tel:+81-80-1234-5678' // Japan
        '+61 4 1234 5678'           || 'tel:+61-412-345-678' // Australia
        '+61412345678'              || 'tel:+61-412-345-678' // Australia
        '+86 139 1099 8888'         || 'tel:+86-139-1099-8888' // China
        '+8613910998888'            || 'tel:+86-139-1099-8888' // China
    }

    def "format with english and E164"() {
        when:
        String result = PhoneNumberUtil.format(number, 'US', PhoneNumberFormat.E164)

        then:
        result == expected

        where:
        number                      || expected
        '510 874 4567'              || '+15108744567'
        '+1 510 874 4567'           || '+15108744567'
        '5108744567'                || '+15108744567'
        '+15108744567'              || '+15108744567'
        '(510) 874-4567'            || '+15108744567'
        '+1 (510) 874-4567'         || '+15108744567'
        '+1 (510) 874-4567;ext=123' || '+15108744567'
        '+1 (510) 874-4567;ext=123' || '+15108744567'
        '+45 4044 1234'             || '+4540441234' // Denmark
        '+45 40 44 12 34'           || '+4540441234' // Denmark
        '+45 40441234'              || '+4540441234' // Denmark
        '+4540441234'               || '+4540441234' // Denmark
        '+81 80-1234-5678'          || '+818012345678' // Japan
        '+818012345678'             || '+818012345678' // Japan
        '+61 4 1234 5678'           || '+61412345678' // Australia
        '+61412345678'              || '+61412345678' // Australia
        '+86 139 1099 8888'         || '+8613910998888' // China
        '+8613910998888'            || '+8613910998888' // China
    }

    void "is region valid"() {
        when:
        def result = PhoneNumberUtil.isValidRegionCode(region)

        then:
        result == expectedResult

        where:
        region || expectedResult
        'BR'   || true
        'DK'   || true
        'US'   || true
        'EN'   || false
        'GB'   || true
    }

}
