package io.hhplus.tdd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.hhplus.tdd.point.UserPoint;

public class UserPointTest {

	
	@Test
	@DisplayName("포인트 충전")
	void charge() {
		// given 
		UserPoint userPoint = new UserPoint(1L, 100L, System.currentTimeMillis());
		
		// when
		UserPoint resultPoint = userPoint.charge(250L);
		
		// then
		assertThat(resultPoint.id()).isEqualTo(1L);
		assertThat(resultPoint.point()).isEqualTo(350L);
	}
	
	@Test
	@DisplayName("최대 포인트 초과 충전 실패")
	void chargeMinus() {
		// given
		UserPoint userPoint = new UserPoint(1L, 1000L, System.currentTimeMillis());
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> userPoint.charge(9001L));
	}
	
	@Test
	@DisplayName("포인트 사용")
	void use() {
		// given 
		UserPoint userPoint = new UserPoint(1L, 100L, System.currentTimeMillis());
		
		// when
		UserPoint resultPoint = userPoint.use(10L);
		
		// then
		assertThat(resultPoint.id()).isEqualTo(1L);
		assertThat(resultPoint.point()).isEqualTo(90L);
	}
	
	@Test
	@DisplayName("포인트 사용 잔고 부족")
	void useMinus() {
		// given
		UserPoint userPoint = new UserPoint(1L, 100L, System.currentTimeMillis());
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> userPoint.use(101L));		
	}
	
	@Test
	@DisplayName("UserPoint 생성시 아이디 0이하일 경우 실패")
	void userPoint() {
		// given
		long id = 0L;
		long point = 100L;
		long updateMillis = System.currentTimeMillis();
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> new UserPoint(id, point, updateMillis));
		
	}
}
