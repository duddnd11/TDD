package io.hhplus.tdd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointHistoryRepository;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.UserPointRepository;
import io.hhplus.tdd.point.UserPointServiceImpl;

public class UserPointServiceTest {

	@Mock
	UserPointRepository userPointRepository;
	
	@Mock
	PointHistoryRepository pointHistoryRepository;
	
	@InjectMocks // 인터페이스 사용불가, 구현체에서만 사용가능
	UserPointServiceImpl userPointService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);  // Mockito 초기화
	}
	
	@Test
	@DisplayName("포인트 충전")
	void chargePoint() {
		// given
		long id = 1L;
		long amount = 100L;
		UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());
		PointHistory pointHistory = new PointHistory(0, id, amount, TransactionType.CHARGE, System.currentTimeMillis());
		
		// stub => when: 특정 메서드 호출에대한 원하는 결과를 반환 하도록 설정
		when(userPointRepository.findById(id)).thenReturn(new UserPoint(id,10L,System.currentTimeMillis()));
		when(userPointRepository.save(userPoint)).thenReturn(userPoint);
		when(pointHistoryRepository.save(pointHistory)).thenReturn(pointHistory);
		
		// when
		UserPoint resultUserPoint = userPointService.charge(id, amount);
		
		// then
		assertThat(resultUserPoint.point()).isEqualTo(110L);
	}
	
	@Test
	@DisplayName("0이하 포인트 충전 실패")
	void chargePointMinus() {
		//given
		long id = 1L;
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> userPointService.charge(id, 0L));
		assertThrows(IllegalArgumentException.class, () -> userPointService.charge(id, -1L));
	}
	
	@Test
	@DisplayName("최대 잔고 이상 충전 시 실패")
	void chargePointMax() {
		//given
		long id = 1L;
		
		when(userPointRepository.findById(id)).thenReturn(new UserPoint(id, 0L, System.currentTimeMillis()));
		
		assertThrows(IllegalArgumentException.class, () -> userPointService.charge(id, 10001L));
	}
	
	@Test
	@DisplayName("포인트 사용")
	void usePoint() {
		//given
		long id = 1L;
		long amount = 20L;
		UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());
		PointHistory pointHistory = new PointHistory(0, id, amount, TransactionType.USE, System.currentTimeMillis());
		
		when(userPointRepository.findById(id)).thenReturn(new UserPoint(id, 100L, System.currentTimeMillis()));
		when(userPointRepository.save(userPoint)).thenReturn(userPoint);
		when(pointHistoryRepository.save(pointHistory)).thenReturn(pointHistory);
		
		// when
		UserPoint resultUserPoint = userPointService.use(id, amount);
		
		// then
		assertThat(resultUserPoint.point()).isEqualTo(80L);
	}
	
	@Test
	@DisplayName("0이하 포인트 사용 실패")
	void usePointMinus() {
		//given
		long id = 1L;
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> userPointService.use(id, 0L));
		assertThrows(IllegalArgumentException.class, () -> userPointService.use(id, -1L));
	}
	
	@Test
	@DisplayName("포인트 사용 잔고 부족")
	void usePointMin() {
		//given
		long id = 1L;
		
		when(userPointRepository.findById(id)).thenReturn(new UserPoint(id, 100L, System.currentTimeMillis()));
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> userPointService.use(id, 101L));
	}
	
	@Test
	@DisplayName("포인트 조회")
	void findPoint() {
		// given
		long id = 1L;
		long point = 100L;
		UserPoint userPoint = new UserPoint(id, point, System.currentTimeMillis());
		when(userPointRepository.findById(id)).thenReturn(userPoint);
		
		// when
		UserPoint resultUserPoint = userPointService.findPointById(id);
		
		// then
		assertThat(resultUserPoint.point()).isEqualTo(point);
	}
	
	@Test
	@DisplayName("포인트 조회 사용자 아이디 1미만 실패")
	void findPointMinusId() {
		// given
		long id = 0L;
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> userPointService.findPointById(id));
	}
}
