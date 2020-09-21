package com.manish.statisticservice.repository;

import com.manish.statisticservice.domain.DataPoint;
import com.manish.statisticservice.domain.DataPointId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataPointRepository extends CrudRepository<DataPoint, DataPointId> {

	List<DataPoint> findByIdAccount(String account);

}
