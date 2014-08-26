package com.bertramlabs.plugins.selfie

import org.codehaus.groovy.grails.validation.AbstractConstraint

class ContentTypeConstraint extends AbstractConstraint {

	public boolean supports(Class classObject) {
			classObject == Attachment
	}

	public String getName() {
		return "contentType"
	}

	protected void processValidate(java.lang.Object target, java.lang.Object propertyValue, org.springframework.validation.Errors errors) {
		def contentType = propertyValue.contentType
		println "Validation of ContentType ${contentType}"
		if(constraintParameter instanceof List) {
			if(!constraintParameter.contains(contentType)) {
				println "Rejecting Content Type ${propertyValue.contentType}"
				rejectValue target, errors, "default.invalid.${name}.message", "${name}.invalid", [constraintPropertyName, constraintOwningClass, contentType] as Object[]
			}
		} else if (constraintParameter != contentType) {
			rejectValue target, errors, "default.invalid.${name}.message", "${name}.invalid", [constraintPropertyName, constraintOwningClass, contentType] as Object[]
		}
	}

	protected boolean	skipBlankValues() {
		return true
	}
	protected boolean	skipNullValues() {
		return true
	}
}
