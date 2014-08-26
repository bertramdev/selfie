package com.bertramlabs.plugins.selfie

// import org.hibernate.usertype.UserType
import org.hibernate.usertype.CompositeUserType
import java.sql.PreparedStatement
import org.hibernate.HibernateException
import java.sql.ResultSet
import java.sql.SQLException
import org.hibernate.type.Type;
import org.hibernate.engine.SessionImplementor;
import org.springframework.util.ObjectUtils;

class AttachmentUserType implements CompositeUserType {
	public String[] getPropertyNames() {
		return ["fileName","fileSize","contentType"] as String[]
	}

	/**
	* This refers to java object property types
	*/
	public Type[] getPropertyTypes() {
		return [org.hibernate.type.StandardBasicTypes.STRING, org.hibernate.type.StandardBasicTypes.LONG, org.hibernate.type.StandardBasicTypes.STRING] as Type
	}

	/**
	* This method fetches the property from the user type depending upon
	* the index.It should follow getPropertyNames()s
	*/
	public Object getPropertyValue(Object component, int property)
	throws HibernateException {
		if(component ==null)
		return null;
		else{
			if(property==0 )
			return component.fileName
			else if(property==1)
			return component.fileSize
			else if(property==2)
			return component.contentType
		}

		return null;
	}

	/**
	* This method sets the individual property in the custom user type
	*/
	public void setPropertyValue(Object component, int property, Object value)
	throws HibernateException {
		println "Setting Property Values ${property} ${value}"
		if(value!=null){
			if (property ==0){
				component.fileName = (String)value;
			}else if(property ==1) {
				component.fileSize = (Long)value;
			} else if(property == 2) {
				component.contentType = (String)value;
			}
		}
	}

	/**
	* This method returns the custom user type class
	*/
	public Class returnedClass() {
		return Attachment.class;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		return ObjectUtils.nullSafeEquals(x, y);
	}

	public int hashCode(Object x) throws HibernateException {
		if (x!=null)
		return x.hashCode();
		else
		return 0;
	}

	/**
	* This method constructs the custom user type from the resultset
	*/
	public Object nullSafeGet(ResultSet rs, String[] names,
	SessionImplementor session, Object owner)
	throws HibernateException, SQLException {
		String fileName    = rs.getString(names[0])
		Long fileSize      = rs.getLong(names[1])
		String contentType = rs.getString(names[2])
		println "Executing a null Safe Get ${fileName} ${names[0]}"
		if(fileName != null) {
			return new Attachment(fileName: fileName, fileSize: fileSize, contentType: contentType)
		} else {
			return null
		}
	}

	/**
	* This method sets the value from the user type into prepared statement
	*/
	public void nullSafeSet(PreparedStatement st, Object value, int index,
	SessionImplementor session) throws HibernateException, SQLException {
		if(value !=null){
			st.setString(index,((Attachment)value).fileName);
			st.setLong(index+1,((Attachment)value).fileSize);
			st.setString(index+2,((Attachment)value).contentType);
		}else{
			st.setObject(index,null);
			st.setObject(index +1, null);
			st.setObject(index +2, null);
		}
		// (Attachment)value.save()
	}

	/**
	* Deep copy
	*/
	public Object deepCopy(Object value) throws HibernateException {
		Attachment returnVal = new Attachment()
		Attachment currVal = (Attachment)value
		if(currVal) {
			returnVal.fileName = currVal.fileName ? new String(currVal.fileName) : null
			returnVal.fileSize = currVal.fileSize ? new Long(currVal.fileSize) : null
			returnVal.contentType = currVal.contentType ? new String(currVal.contentType) : null
		} else {
			return null
		}

		return returnVal;
	}

	public boolean isMutable() {
		return false;
	}

	public Serializable disassemble(Object value, SessionImplementor session)
	throws HibernateException {
		Object  deepCopy=deepCopy(value);

		if(!(deepCopy instanceof Serializable))
		return (Serializable)deepCopy;

		return null;
	}

	public Object assemble(Serializable cached, SessionImplementor session,
	Object owner) throws HibernateException {
		return deepCopy(cached);
	}

	public Object replace(Object original, Object target,
	SessionImplementor session, Object owner) throws HibernateException {
		return deepCopy(original);
	}

}
