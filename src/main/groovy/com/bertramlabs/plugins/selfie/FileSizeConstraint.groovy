package com.bertramlabs.plugins.selfie

import grails.validation.AbstractConstraint
import org.springframework.validation.Errors
import groovy.transform.CompileStatic

@CompileStatic
class FileSizeConstraint extends AbstractConstraint {

	boolean supports(Class classObject) {
		classObject == Attachment
	}

	String getName() { "fileSize" }

	protected void processValidate(target, propertyValue, Errors errors) {
		Attachment attachment = (Attachment) propertyValue

		Long fileSize = attachment.fileSize ?: 0
		if (constraintParameter instanceof Map) {
			if (constraintParameter.min) {
				if (fileSize < (constraintParameter.min as Long)) {
					rejectValue target, errors, "default.invalid.${name}.message", "${name}.invalid", [constraintPropertyName, constraintOwningClass, fileSize] as Object[]
				}
			}
			if (constraintParameter.max) {
				if (fileSize > (constraintParameter.max as Long)) {
					rejectValue target, errors, "default.invalid.${name}.message", "${name}.invalid", [constraintPropertyName, constraintOwningClass, fileSize] as Object[]
				}
			}
		}
		else if ((constraintParameter as Long) < fileSize) {
			rejectValue target, errors, "default.invalid.${name}.message", "${name}.invalid", [constraintPropertyName, constraintOwningClass, fileSize] as Object[]
		}
	}

	protected boolean skipBlankValues() { true }
	protected boolean skipNullValues() { true }
}
