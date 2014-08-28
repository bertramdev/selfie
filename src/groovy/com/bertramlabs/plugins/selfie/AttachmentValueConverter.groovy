package com.bertramlabs.plugins.selfie

import org.grails.databinding.converters.ValueConverter
import org.springframework.web.multipart.commons.CommonsMultipartFile

class AttachmentValueConverter implements ValueConverter {

	boolean canConvert(value) {
		value instanceof CommonsMultipartFile
	}

	def convert(value) {
		if (!value.originalFilename) {
			return null
		}

		new Attachment(contentType: value.contentType,originalFilename: value.originalFilename, fileSize: value.size, inputStream: value.inputStream)
	}

	Class<?> getTargetType() { Attachment }
}
