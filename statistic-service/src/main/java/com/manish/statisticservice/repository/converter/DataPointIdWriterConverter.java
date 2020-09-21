package com.manish.statisticservice.repository.converter;

import com.manish.statisticservice.domain.DataPointId;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DataPointIdWriterConverter implements Converter<DataPointId, DBObject> {

	// number of properties/fields in the object.
	private static final int FIELDS = 2;

	@Override
	public DBObject convert(DataPointId id) {

		DBObject object = new BasicDBObject(FIELDS);

		object.put("date", id.getDate());
		object.put("account", id.getAccount());

		return object;
	}
}
