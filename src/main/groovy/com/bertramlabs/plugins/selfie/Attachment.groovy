package com.bertramlabs.plugins.selfie

import grails.util.GrailsNameUtils
import grails.util.Holders

import com.bertramlabs.plugins.karman.*
import com.bertramlabs.plugins.selfie.processors.ImageResizer
import java.security.MessageDigest

class Attachment {
	static transients = ['originalFilename','propertyName','options','parentEntity','processors','domainName','fileStream','cloudFile','storageOptions','config','styles','inputStream']
	String fileName
	String contentType
	Long fileSize
	String originalFilename


	String propertyName
	String domainName
	def options =[:]
	def parentEntity
	def processors = [ImageResizer]

	InputStream fileStream

	
	static constraints = {
		contentType nullable:true
		fileName nullable:true
		fileSize nullable:true
	}
	static getFilename(String filename) {
		filename?.lastIndexOf('.') >= 0 ? filename.substring(0, filename.lastIndexOf('.')) : filename
	}

	static getExtension(String filename) {
		filename?.lastIndexOf('.') >= 0 ? filename?.substring(filename?.lastIndexOf('.'), filename.length()) : ''
	}

	def url(String typeName, expiration=null) {
		def storageOptions = getStorageOptions(domainName,propertyName)
		def typeFileName = fileNameForType(typeName)
		def cloudFile = getCloudFile(typeName)
		def url
		if(!parentEntity) {
			return null
		}

		if(!storageOptions.url) {
			url = cloudFile.getURL(expiration).toString()
		} else {
			url = evaluatedPath((storageOptions.url ?: '/'),typeName) + typeFileName
		}

		url
	}

	def url(Map styleOptions, expiration=null) {

		def styleHashString = styleOptions.sort{it.key}.toString()
		MessageDigest md = MessageDigest.getInstance("MD5")
        md.update(styleHashString.bytes)
        def checksum = md.digest()
        def styleHash = checksum.encodeHex().toString()
		def storageOptions = getStorageOptions(domainName,propertyName)
		def typeFileName = fileNameForType(styleHash)
		def cloudFile = getCloudFile(styleHash)
		def url


		if(!cloudFile.exists()) {
			for (processorClass in processors) {
				processorClass.newInstance(attachment: this).process(styleHash,styleOptions)
			}
			if(!cloudFile.exists()) {
				return null
			}
		}

		if(!storageOptions.url) {
			url = cloudFile.getURL(expiration).toString()
		} else {
			url = evaluatedPath((storageOptions.url ?: '/'),styleHash) + typeFileName
		}

		url
	}

	def getOptions() {
		def evaluatedOptions = options.clone()
		if(evaluatedOptions.styles && evaluatedOptions.styles instanceof Closure) {
			evalutedOptions.styles = evaluatedOptions.styles.call(attachment)
		}
		evaluatedOptions?.styles?.each { style ->
			if(style.value instanceof Closure) {
				style.value = style.value.call(attachment)
			}
		}
		return evaluatedOptions
	}

	public Long getFileSize() {
		if(fileSize) {
			return fileSize
		}
		if(cloudFile?.exists()) {
			fileSize = cloudFile?.contentLength
		}
		return fileSize
	}

	public String getContentType() {
		if(contentType) {
			return contentType
		}
		if(cloudFile.exists()) {
			contentType = cloudFile?.contentType	
		}
		
		return contentType
	}

	void setInputStream(is) {
		fileStream = is
	}

 	def getInputStream() {
 		if(cloudFile.exists()) {
			return cloudFile.inputStream
		}
		return null
	}

 	def getCloudFile(typeName='original') {
		if(!typeName) {
			typeName = 'original'
		}
		if(!parentEntity) {
			return null
		}
		def storageOptions = getStorageOptions(domainName,propertyName)
		def bucket = storageOptions.bucket ?: '.'
		def path = storageOptions.path ?: ''
		def provider = StorageProvider.create(storageOptions.providerOptions.clone())
		def typeFileName = fileNameForType(typeName)
		return provider[bucket][evaluatedPath(path,typeName) + typeFileName]
	}

	void save() {
		def storageOptions = getStorageOptions(domainName,propertyName)
		def bucket = storageOptions.bucket ?: '.'
		def path = storageOptions.path ?: ''
		def provider = StorageProvider.create(storageOptions.providerOptions.clone())

		// First lets upload the original
		if(fileStream && fileName) {
			def cloudFile = provider[bucket][ evaluatedPath(path,'original') + fileNameForType('original')]
			cloudFile.contentType = contentType
			if(fileSize) {
				cloudFile.contentLength = fileSize
				cloudFile.setInputStream(fileStream)	
			} else {
				cloudFile.bytes = fileStream.bytes
			}
			
			cloudFile.save()
			reprocessStyles()
			fileStream = null
		}


	}

	def saveProcessedStyle(typeName, bytes) {
		def cloudFile = getCloudFile(typeName)
		cloudFile.bytes = bytes
		cloudFile.save()
	}

	void delete() {
		def storageOptions = getStorageOptions(domainName,propertyName)
		def path = storageOptions.path ?: ''
		def provider = StorageProvider.create(storageOptions.providerOptions.clone())
		def bucket = storageOptions.bucket ?: '.'

		for (type in styles) {
			def cloudFile = provider[bucket][evaluatedPath(path,type) + fileNameForType(type)]
			if(cloudFile.exists()) {
				cloudFile.delete()
			}
		}
	}

	String generateBaseFileName() {
		fileName ?: originalFilename
	}

	void setOriginalFilename(String name) {
		originalFilename = name
        fileName = fileName ?: originalFilename
	}

	String fileNameForType(typeName) {
		def fileNameWithOutExt = getFilename(fileName)
		def extension = getExtension(fileName)

		"${fileNameWithOutExt}_${typeName}${extension}"
	}

	def getStyles() {
		def types = ['original']
		types.addAll options?.styles?.collect { it.key} ?: []
		types
	}

	protected String evaluatedPath(String input,type='original') {
		def path = input?.replace(":class","${GrailsNameUtils.getShortName(parentEntity.getClass())}").replace(":id","${parentEntity.id}").replace(":type","${type}").replace(":style","${type}").replace(":propertyName","${propertyName}")
		options?.storage?.pathLowerCase ? path.toLowerCase() : path
	}

	void reprocessStyles() {
		for (processorClass in processors) {
			processorClass.newInstance(attachment: this).process()
		}
		// TODO: Grab Original File and Start Building out Thumbnails
	}

	protected getConfig() {
		Holders.getConfig()?.grails?.plugin?.selfie
	}

	protected getStorageOptions(name, propertyName) {
		def options = ((config?.domain?."${name}"?."${propertyName}"?.storage ?: config?.domain?."${name}"?.storage  ?: config?.storage ?: [:]) + (options?.storage ?: [:])).clone()
		if(options.providerOptions && !options.providerOptions.containsKey('defaultFileACL')) {
          options.providerOptions.defaultFileACL = com.bertramlabs.plugins.karman.CloudFileACL.PublicRead
		}

		if(!options.containsKey('path')) {
			options.path = 'uploads/:class/:id/:propertyName/'
		}
		options
	}
}
