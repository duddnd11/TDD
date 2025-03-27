package io.hhplus.tdd.point;

public interface UserPointService {
	UserPoint charge(long id, long amount);
	
	UserPoint use(long id, long amount);
	
	UserPoint findPointById(long id);
}
