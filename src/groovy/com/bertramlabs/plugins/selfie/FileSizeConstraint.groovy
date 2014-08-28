package com.bertramlabs.plugins.selfie

import org.codehaus.groovy.grails.validation.AbstractConstraint
import org.springframework.validation.Errors

class FileSizeConstraint extends AbstractConstraint {

	boolean supports(Class classObject) {
		classObject == Attachment
	}

	String getName() { "fileSize" }

	protected void processValidate(target, propertyValue, Errors errors) {
		def fileSize = propertyValue.fileSize
		if (constraintParameter instanceof Map) {
			if (constraintParameter.min) {
				if (fileSize < constraintParameter.min) {
					rejectValue target, errors, "default.invalid.${name}.message", "${name}.invalid", [constraintPropertyName, constraintOwningClass, fileSize] as Object[]
				}
			}
			if (constraintParameter.max) {
				if (fileSize > constraintParameter.max) {
					rejectValue target, errors, "default.invalid.${name}.message", "${name}.invalid", [constraintPropertyName, constraintOwningClass, fileSize] as Object[]
				}
			}
		}
		else if (constraintParameter < fileSize) {
			rejectValue target, errors, "default.invalid.${name}.message", "${name}.invalid", [constraintPropertyName, constraintOwningClass, fileSize] as Object[]
		}
	}

	protected boolean skipBlankValues() { true }
	protected boolean skipNullValues() { true }
}
