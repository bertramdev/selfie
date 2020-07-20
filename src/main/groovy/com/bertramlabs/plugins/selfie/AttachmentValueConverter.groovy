package com.bertramlabs.plugins.selfie

import grails.databinding.converters.ValueConverter
import org.springframework.web.multipart.MultipartFile

class AttachmentValueConverter implements ValueConverter {

    static boolean isNull(value) {
        value == null || 'null' == value
    }

    boolean canConvert(value) {
        value instanceof MultipartFile || isNull(value)
    }

    def convert(value) {
        if (isNull(value) || !(value instanceof MultipartFile) || !((MultipartFile) value).originalFilename) {
            return null
        }
        def originalFilename = value.originalFilename.replace('../','').replace('./','').replace('/','')
        new Attachment(contentType: value.contentType, originalFilename: originalFilename, fileSize: value.size, inputStream: value.inputStream)
    }

    Class<?> getTargetType() {
        return Attachment
    }
}
