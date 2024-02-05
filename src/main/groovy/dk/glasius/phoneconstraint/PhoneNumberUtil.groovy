package dk.glasius.phoneconstraint

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat
import com.google.i18n.phonenumbers.Phonenumber
import groovy.transform.CompileStatic

import static com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance

@CompileStatic
class PhoneNumberUtil {

    static boolean isValid(String number, String defaultRegion = null) {
        try {
            Phonenumber.PhoneNumber phoneNumber = getPhoneNumber(number, defaultRegion)
            instance.isValidNumber(phoneNumber)
        } catch (NumberParseException ignore) {
            return false
        }
    }

    static String format(String number, String defaultRegion = null, PhoneNumberFormat defaultFormat = null) {
        Phonenumber.PhoneNumber phone = getPhoneNumber(number, defaultRegion)
        PhoneNumberFormat phoneNumberFormat = defaultFormat ?: PhoneNumberFormat.INTERNATIONAL
        instance.format(phone, phoneNumberFormat)
    }

    static boolean isValidRegionCode(String regionCode) {
        return regionCode != null && instance.supportedRegions.contains(regionCode)
    }

    protected static Phonenumber.PhoneNumber getPhoneNumber(String number, String defaultRegion = null) {
        instance.parse(number, defaultRegion ?: 'US')
    }

}
