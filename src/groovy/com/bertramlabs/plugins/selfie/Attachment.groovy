package com.bertramlabs.plugins.selfie
import grails.util.Holders
import com.bertramlabs.plugins.karman.*

class Attachment {
	String fileName
	String contentType
	Long fileSize
	String originalFilename


	String propertyName
	String domainName
	def options =[:]

	InputStream fileStream

	public url(typeName) {
		def typeFileName = fileNameForType(typeName)
		def fileOptions = getStorageOptions(propertyName,domainName) + (options?.storage ?: [:])
		println "FileOptions ${fileOptions}"
		def url = (fileOptions.url ?: '/') + typeFileName
		return url
	}

	public setInputStream(is) {
		fileStream = is
	}

	public save(options) {
		def storageOptions = getStorageOptions(options.domain,options.name) ?: [:]
		storageOptions += options.storage
		def bucket = storageOptions.bucket ?: '.'

		println "initializing a Provider ${storageOptions.providerOptions.clone()}"
		def provider = StorageProvider.create(storageOptions.providerOptions.clone())

		// First lets upload the original
		if(fileStream && fileName) {
			provider[bucket][fileNameForType('original')] = fileStream.bytes
		}
	}

	public delete() {
		def storageOptions = getStorageOptions(propertyName,domainName) + (options?.storage ?: [:])

		def provider = StorageProvider.create(storageOptions.providerOptions.clone())
		def bucket = storageOptions.bucket ?: '.'

		types.each { type ->
			def cloudFile = provider[bucket][fileNameForType(type)]
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


	public rebuildThumbs() {
		// TODO: Grab Original File and Start Building out Thumbnails
	}

	protected getConfig() {
		Holders.getConfig()?.grails?.plugin?.selfie
	}

	protected getStorageOptions(name, propertyName) {
		return config?.domain?."${name}"?."${propertyName}"?.storage ?: config?.domain?."${name}"?.storage  ?: config?.storage
	}
}
