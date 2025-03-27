package io.hhplus.tdd.point;

import io.hhplus.tdd.ValidationUtil;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {
	private static final long MAX_POINT = 10000L;
	
	public UserPoint{
		ValidationUtil.validateUnderOne(id);
	}
	
    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }
    
    public UserPoint charge(long amount) {
    	long pointTotal = point + amount;
    	if(pointTotal > MAX_POINT ) {
    		throw new IllegalArgumentException("최대 10000 포인트를 넘을 수 없습니다.");
    	}
    	return new UserPoint(id, pointTotal, System.currentTimeMillis());
    }
    
    public UserPoint use(long amount) {
    	long pointTotal = point - amount;
    	if(pointTotal < 0L ) {
    		throw new IllegalArgumentException("포인트 잔고가 부족합니다.");
    	}
    	return new UserPoint(id, pointTotal, System.currentTimeMillis());
    }
}

