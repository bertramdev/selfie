package com.bertramlabs.plugins.selfie
import grails.util.Holders
import com.bertramlabs.plugins.karman.*
import grails.util.GrailsNameUtils

import com.bertramlabs.plugins.selfie.processors.ImageResizer

class Attachment {
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

	public url(typeName, expiration=null) {
		def storageOptions = getStorageOptions(propertyName,domainName)
		def typeFileName = fileNameForType(typeName)
		def cloudFile = this.getCloudFile(typeName)
		def url

		if(!storageOptions.url) {
			url = cloudFile.getURL(expiration).toString()
		} else {
			url = evaluatedPath((storageOptions.url ?: '/'),typeName) + typeFileName
		}

		return url
	}

	public setInputStream(is) {
		fileStream = is
	}

 	def getInputStream() {
		return cloudFile.inputStream

		if(fileStream) {
			return fileStream
		} else {
			return cloudFile.inputStream
		}
	}

 	def getCloudFile(typeName=null) {
			if(!typeName) {
				typeName = 'original'
			}
			def storageOptions = getStorageOptions(propertyName,domainName)
			def bucket = storageOptions.bucket ?: '.'
			def path = storageOptions.path ?: ''
			def provider = StorageProvider.create(storageOptions.providerOptions.clone())
			def typeFileName = fileNameForType(typeName)
			return provider[bucket][evaluatedPath(path,typeName) + typeFileName]
	}

	public save() {
		def storageOptions = getStorageOptions(propertyName,domainName)
		def bucket = storageOptions.bucket ?: '.'
		def path = storageOptions.path ?: ''
		def provider = StorageProvider.create(storageOptions.providerOptions.clone())

		// First lets upload the original
		if(fileStream && fileName) {
			provider[bucket][ evaluatedPath(path,'original') + fileNameForType('original')] = fileStream.bytes
			reprocessStyles()
		}


	}

	def saveProcessedStyle(typeName, bytes) {
		def cloudFile = getCloudFile(typeName)
		cloudFile.bytes = bytes
		cloudFile.save()
	}

	public delete() {
		def storageOptions = getStorageOptions(propertyName,domainName)
		def path = storageOptions.path ?: ''
		def provider = StorageProvider.create(storageOptions.providerOptions.clone())
		def bucket = storageOptions.bucket ?: '.'

		styles.each { type ->
			def cloudFile = provider[bucket][evaluatedPath(path,type) + fileNameForType(type)]
			if(cloudFile.exists()) {
				cloudFile.delete()
			}
		}
	}

	public generateBaseFileName() {
		return fileName ?: originalFilename
	}

	public void setOriginalFilename(String name) {
		originalFilename = name
		fileName = fileName ?: originalFilename
	}


	public fileNameForType(typeName) {
		def fileNameWithOutExt = fileName.replaceFirst(/[.][^.]+$/, "");
		def extension = (fileName =~ /[.]([^.]+)$/)[0][1]
		return "${fileNameWithOutExt}_${typeName}.${extension}"
	}

	public getStyles() {
		def types = ['original']
		types += options?.styles?.collect { it.key} ?: []
		return types
	}

	protected String evaluatedPath(String input,type='original') {
		input?.replace(":class","${GrailsNameUtils.getShortName(parentEntity.class)}").replace(":id","${parentEntity.id}").replace(":type","${type}").replace(":style","${type}").replace(":propertyName","${propertyName}")
	}


	public reprocessStyles() {
		processors.each { processorClass ->
			def processor = processorClass.newInstance(attachment: this)
			processor.process()
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
		return options
	}
}
