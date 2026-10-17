package dk.glasius.phoneconstraint

import org.grails.plugins.i18n.I18nDescriptor
import org.grails.plugins.i18n.I18nDescriptors
import org.springframework.context.support.ResourceBundleMessageSource
import spock.lang.Specification

/**
 * Grails 8 resolves plugin messages through Spring Boot's message source, using the base names listed in each
 * artifact's generated {@code META-INF/grails/i18n.properties}. This checks the plugin's descriptor and bundle line up,
 * so {@code default.invalid.phoneNumber.message} is found at runtime instead of falling back to a generic message.
 */
class PhoneNumberMessageBundleSpec extends Specification {

    void "plugin i18n descriptor lists the plugin's message bundle"() {
        when:
        I18nDescriptor descriptor = pluginDescriptor()

        then:
        descriptor
        !descriptor.isApplication()
        descriptor.basenames() == ['grails-phone-number-constraint']
    }

    void "default phone number message resolves from the bundles in the plugin descriptor"() {
        given: 'a message source configured the way Grails 8 configures Spring Boot from the descriptor'
        def messageSource = new ResourceBundleMessageSource(
                basenames: pluginDescriptor().basenames() as String[],
                fallbackToSystemLocale: false,
                defaultEncoding: 'UTF-8'
        )

        when:
        String message = messageSource.getMessage(
                PhoneNumberConstraint.DEFAULT_INVALID_PHONE_NUMBER_MESSAGE_CODE,
                ['number', 'PhoneNumber', '1234'] as Object[],
                Locale.ENGLISH
        )

        then:
        message == 'Property [number] of class [PhoneNumber] with value [1234] does not pass phone-number validation'
    }

    private static I18nDescriptor pluginDescriptor() {
        I18nDescriptors.load(PhoneNumberMessageBundleSpec.classLoader)
                .find { it.name() == 'grails-phone-number-constraint' }
    }
}
