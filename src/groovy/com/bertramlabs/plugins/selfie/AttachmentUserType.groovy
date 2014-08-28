package com.bertramlabs.plugins.selfie

// import org.hibernate.usertype.UserType
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Types

import org.hibernate.HibernateException
import org.hibernate.engine.SessionImplementor
import org.hibernate.type.StandardBasicTypes
import org.hibernate.type.Type
import org.springframework.util.ObjectUtils
import org.hibernate.usertype.CompositeUserType

class AttachmentUserType implements CompositeUserType {

	final String[] propertyNames = ["fileName","fileSize","contentType"]

	final Type[] propertyTypes = [StandardBasicTypes.STRING, StandardBasicTypes.LONG, StandardBasicTypes.STRING]

	def getPropertyValue(component, int property) {
		if (component == null) {
			return null
		}

		switch (property) {
			case 0: return component.fileName
			case 1: return component.fileSize
			case 2: return component.contentType
		}
	}

	void setPropertyValue(component, int property, value) {
		if (value == null) {
			return
		}

		switch (property) {
			case 0: component.fileName = value; break
			case 1: component.fileSize = value; break
			case 2: component.contentType = value; break
		}
	}

	Class returnedClass() { Attachment }

	boolean equals(x, y) {
		ObjectUtils.nullSafeEquals(x, y)
	}

	int hashCode(Object x) {
		x == null ? 0 : x.hashCode()
	}

	def nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, owner) throws SQLException {
		String fileName    = rs.getString(names[0])
		Long fileSize      = rs.getLong(names[1])
		String contentType = rs.getString(names[2])
		fileName == null ? null : new Attachment(fileName: fileName, fileSize: fileSize, contentType: contentType)
	}

	void nullSafeSet(PreparedStatement st, value, int index, SessionImplementor session) throws SQLException {
		if (value == null) {
			st.setNull(index, Types.VARCHAR)
			st.setNull(index, Types.BIGINT)
			st.setNull(index, Types.VARCHAR)
		}
		else {
			st.setString(index, value.fileName)
			st.setLong(index + 1, value.fileSize)
			st.setString(index + 2, value.contentType)
		}
		// (Attachment)value.save()
	}

	def deepCopy(value) {
		if (value) {
			return new Attachment(fileName: value.fileName, fileSize: value.fileSize, contentType: value.contentType)
		}
	}

	boolean isMutable() { false }

	Serializable disassemble(value, SessionImplementor session) {
		def deepCopy = deepCopy(value)
		deepCopy instanceof Serializable ? deepCopy : null
	}

	def assemble(Serializable cached, SessionImplementor session, owner) {
		deepCopy(cached)
	}

	def replace(original, target, SessionImplementor session, owner) {
		deepCopy(original)
	}
}
