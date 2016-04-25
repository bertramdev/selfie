package com.bertramlabs.plugins.selfie

import org.codehaus.groovy.grails.validation.AbstractConstraint
import org.springframework.validation.Errors

class ContentTypeConstraint extends AbstractConstraint {

	boolean supports(Class classObject) {
		classObject == Attachment
	}

	String getName() { "contentType" }

	protected void processValidate(target, propertyValue, Errors errors) {
		def contentType = propertyValue?.contentType
		if (constraintParameter instanceof List) {
			if (!constraintParameter.contains(contentType)) {
				rejectValue target, errors, "default.invalid.${name}.message", "${name}.invalid", [constraintPropertyName, constraintOwningClass, contentType] as Object[]
			}
		} else if (constraintParameter != contentType) {
			rejectValue target, errors, "default.invalid.${name}.message", "${name}.invalid", [constraintPropertyName, constraintOwningClass, contentType] as Object[]
		}
	}

	protected boolean	skipBlankValues() { true }
	protected boolean	skipNullValues() { true }
}
