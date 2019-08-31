package com.bertramlabs.plugins.selfie

import org.grails.datastore.gorm.validation.constraints.AbstractConstraint
import org.springframework.validation.Errors
import groovy.transform.CompileStatic

@CompileStatic
class ContentTypeConstraint extends AbstractConstraint {

	boolean supports(Class classObject) {
		classObject == Attachment
	}

	String getName() { "contentType" }

	protected void processValidate(target, propertyValue, Errors errors) {
		Attachment attachment = (Attachment) propertyValue
		String contentType = attachment?.contentType
		if (constraintParameter instanceof List) {
			if (!(constraintParameter as List<String>).contains(contentType)) {
				rejectValue target, errors, "default.invalid.${name}.message", "${name}.invalid", [constraintPropertyName, constraintOwningClass, contentType] as Object[]
			}
		} else if ((constraintParameter as String) != contentType) {
			rejectValue target, errors, "default.invalid.${name}.message", "${name}.invalid", [constraintPropertyName, constraintOwningClass, contentType] as Object[]
		}
	}

	protected boolean	skipBlankValues() { true }
	protected boolean	skipNullValues() { true }
}
