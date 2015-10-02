package org.openelis.ui.util;

import java.util.Date;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;

@Converter
public class YSConverter implements AttributeConverter<Datetime, Date> {

	@Override
	public Date convertToDatabaseColumn(Datetime attribute) {
		return DataBaseUtil.toDate(attribute);
	}

	@Override
	public Datetime convertToEntityAttribute(Date dbData) {
		return DataBaseUtil.toYS(dbData);
	}

}
