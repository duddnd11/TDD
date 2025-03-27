package io.hhplus.tdd.point;

import java.util.List;

public interface PointHistoryRepository {
	PointHistory save(PointHistory pointHistory);
	
	List<PointHistory> findAllByUserId(long userId);
}
