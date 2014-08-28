package com.bertramlabs.plugins.selfie.processors

import com.bertramlabs.plugins.selfie.Attachment
import javax.imageio.ImageIO
import org.imgscalr.Scalr

class ImageResizer {

	private static final Map<String, String> CONTENT_TYPE_TO_NAME =
		['image/jpeg': 'jpeg', 'image/png': 'png', 'image/bmp': 'bmp', 'image/gif': 'gif']

	Attachment attachment

	def process() {
		def formatName = formatNameFromContentType(attachment.contentType)
		if (!formatName) {
			return
		}
		def styleOptions = attachment.options.styles
		def image = ImageIO.read(attachment.inputStream)
		for (style in styleOptions) {
			processStyle(style.key, [format: formatName] + style.value.clone(),image)
		}
	}

	def processStyle(typeName, options, image) {
		def typeFileName = attachment.fileNameForType(typeName)

		def outputImage

		if(options.mode == 'crop') {
			def mode = Scalr.Mode.FIT_TO_HEIGHT
			if(image.width < image.height) {
				mode = Scalr.Mode.FIT_TO_WIDTH
			}
			outputImage = Scalr.resize(image, Scalr.Method.AUTOMATIC, mode, options.width, options.height, Scalr.OP_ANTIALIAS)
			outputImage = Scalr.crop(outputImage, options.width, options.height, Scalr.OP_ANTIALIAS)
		} else if (options.mode == 'scale') {
			outputImage = Scalr.resize(image, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, options.width, options.height, Scalr.OP_ANTIALIAS)
		}
		def saveStream = new ByteArrayOutputStream()

		ImageIO.write(outputImage, options.format,saveStream)
		attachment.saveProcessedStyle(typeName,saveStream.toByteArray())
	}

	def formatNameFromContentType(contentType) {
		CONTENT_TYPE_TO_NAME[contentType]
	}
}
