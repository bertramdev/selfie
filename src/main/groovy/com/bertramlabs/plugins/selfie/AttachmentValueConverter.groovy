package com.bertramlabs.plugins.selfie

import grails.databinding.converters.ValueConverter
import org.springframework.web.multipart.commons.CommonsMultipartFile
import org.springframework.web.multipart.MultipartFile

class AttachmentValueConverter implements ValueConverter {

	boolean canConvert(value) {
		value instanceof MultipartFile
	}

	def convert(value) {
		if (!value.originalFilename) {
			return null
		}

		new Attachment(contentType: value.contentType,originalFilename: value.originalFilename, fileSize: value.size, inputStream: value.inputStream)
	}

	Class<?> getTargetType() { 
		return Attachment 
	}
}
