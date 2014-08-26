package com.bertramlabs.plugins.selfie

import org.codehaus.groovy.grails.validation.AbstractConstraint

class FileSizeConstraint extends AbstractConstraint {

	public boolean supports(Class classObject) {
			classObject == Attachment
	}

	public String getName() {
		return "fileSize"
	}

	protected void processValidate(java.lang.Object target, java.lang.Object propertyValue, org.springframework.validation.Errors errors) {
		def fileSize = propertyValue.fileSize
		if(constraintParameter instanceof Map) {
			if(constraintParameter.min) {
				if(fileSize < constraintParameter.min) {
					rejectValue target, errors, "default.invalid.${name}.message", "${name}.invalid", [constraintPropertyName, constraintOwningClass, fileSize] as Object[]
				}
			}
			if(constraintParameter.max) {
				if(fileSize > constraintParameter.max) {
					rejectValue target, errors, "default.invalid.${name}.message", "${name}.invalid", [constraintPropertyName, constraintOwningClass, fileSize] as Object[]
				}
			}

		} else if (constraintParameter < fileSize) {
				rejectValue target, errors, "default.invalid.${name}.message", "${name}.invalid", [constraintPropertyName, constraintOwningClass, fileSize] as Object[]
		}
	}

	protected boolean	skipBlankValues() {
		return true
	}
	protected boolean	skipNullValues() {
		return true
	}
}
