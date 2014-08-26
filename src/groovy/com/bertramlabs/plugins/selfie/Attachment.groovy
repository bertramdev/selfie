package com.bertramlabs.plugins.selfie
import grails.util.Holders
import com.bertramlabs.plugins.karman.*
import grails.util.GrailsNameUtils


class Attachment {
	String fileName
	String contentType
	Long fileSize
	String originalFilename


	String propertyName
	String domainName
	def options =[:]
	def parentEntity

	InputStream fileStream

	public url(typeName, expiration=null) {
		def storageOptions = getStorageOptions(propertyName,domainName)
		def path = storageOptions.path ?: ''
		def provider = StorageProvider.create(storageOptions.providerOptions.clone())
		def bucket = storageOptions.bucket ?: '.'
		def typeFileName = fileNameForType(typeName)

		def cloudFile = provider[bucket][evaluatedPath(path,typeName) + typeFileName]
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

	public save() {
		def storageOptions = getStorageOptions(propertyName,domainName)
		def bucket = storageOptions.bucket ?: '.'
		def path = storageOptions.path ?: ''
		def provider = StorageProvider.create(storageOptions.providerOptions.clone())

		// First lets upload the original
		if(fileStream && fileName) {
			provider[bucket][ evaluatedPath(path,'original') + fileNameForType('original')] = fileStream.bytes
		}
	}

	public delete() {
		def storageOptions = getStorageOptions(propertyName,domainName)
		def path = storageOptions.path ?: ''
		def provider = StorageProvider.create(storageOptions.providerOptions.clone())
		def bucket = storageOptions.bucket ?: '.'

		types.each { type ->
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

	public getTypes() {
		def types = ['original']
		types += options?.types?.collect { it.key} ?: []

	}

	protected String evaluatedPath(String input,type='original') {
		input?.replace(":class","${GrailsNameUtils.getShortName(parentEntity.class)}").replace(":id","${parentEntity.id}").replace(":type","${type}").replace(":propertyName","${propertyName}")
	}


	public rebuildThumbs() {
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
