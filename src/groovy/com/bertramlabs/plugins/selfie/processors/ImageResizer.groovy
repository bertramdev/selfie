package com.bertramlabs.plugins.selfie.processors

import com.bertramlabs.plugins.selfie.Attachment
import javax.imageio.ImageIO
import org.imgscalr.Scalr
import groovy.util.logging.Log4j

@Log4j
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
		try {
			def typeFileName = attachment.fileNameForType(typeName)
			def outputImage

			if(options.mode == 'fit') {
				def mode = Scalr.Mode.FIT_TO_HEIGHT
				if(image.width < image.height) {
					mode = Scalr.Mode.FIT_TO_WIDTH
				}
				outputImage = Scalr.resize(image, Scalr.Method.AUTOMATIC, mode, options.width, options.height, Scalr.OP_ANTIALIAS)
				def xOffset = 0
				def yOffset = 0
				if(!options.x) {
					xOffset = Math.floor((outputImage.width - options.width) / 2).toInteger()
				}
				if(!options.y) {
						yOffset = Math.floor((outputImage.height - options.height) / 2).toInteger()
				}

				outputImage = Scalr.crop(outputImage, xOffset,yOffset, options.width - xOffset, options.height - yOffset, Scalr.OP_ANTIALIAS)
			} else if (options.mode == 'crop') {
				outputImage = Scalr.crop(outputImage,options.x ?: 0,options.y ?: 0, options.width, options.height, Scalr.OP_ANTIALIAS)
			} else if (options.mode == 'scale') {
				outputImage = Scalr.resize(image, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, options.width, options.height, Scalr.OP_ANTIALIAS)
			}

			def saveStream = new ByteArrayOutputStream()

			ImageIO.write(outputImage, options.format,saveStream)
			attachment.saveProcessedStyle(typeName,saveStream.toByteArray())
		} catch(e) {
			log.error("Error Processing Uploaded File ${attachment.fileName} - ${typeName}",e)
		}

	}

	def formatNameFromContentType(contentType) {
		CONTENT_TYPE_TO_NAME[contentType]
	}
}
