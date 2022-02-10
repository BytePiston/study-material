package com.sap.sptutorial.jpa.el.customer.model;

import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

public class CustomerNameTransformer implements AttributeTransformer {
	private static final long serialVersionUID = 1L;

	@Override
	public void initialize(AbstractTransformationMapping mapping) {
	}

	@Override
	public Object buildAttributeValue(Record record, Object object, Session session) {
		return "Hello World!";
	}

}
