package com.bertramlabs.plugins.selfie
import org.grails.databinding.converters.ValueConverter

class AttachmentValueConverter implements ValueConverter {

    boolean canConvert(value) {
        value instanceof org.springframework.web.multipart.commons.CommonsMultipartFile
    }

    def convert(value) {
        if(value.originalFilename) {
            def attachment = new Attachment(contentType: value.contentType,originalFilename: value.originalFilename, fileSize: value.size)
            attachment.inputStream = value.inputStream
            return attachment
        }
        return null

    }

    Class<?> getTargetType() {
        return Attachment
    }
}
