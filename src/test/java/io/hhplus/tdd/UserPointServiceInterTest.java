package io.hhplus.tdd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.UserPointRepository;
import io.hhplus.tdd.point.UserPointService;

@SpringBootTest
public class UserPointServiceInterTest {

	@Autowired
	UserPointService userPointService;
	
	@Autowired
	UserPointRepository userRepository;
	
	@Test
	@DisplayName("포인트 충전")
	void charge() {
		// given
		long id = 1L;
		long amount = 100L;
		UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());
		userRepository.save(userPoint);
		
		// when
		UserPoint resultUserPoint = userPointService.charge(id, 30L);
		
		// then
		assertThat(resultUserPoint.point()).isEqualTo(130L);
	}
	
	@Test
	@DisplayName("0이하 포인트 충전 실패")
	void chargeUnderOne() {
		// given
		long id = 1L;
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> userPointService.charge(id, 0L));
	}
	
	@Test
	@DisplayName("최대 잔고 이상 충전 시 실패")
	void chargePointMax() {
		//given
		long id = 1L;
		long amount = 300L;
		UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());
		userRepository.save(userPoint);
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> userPointService.charge(id, 9701L));
	}
	
	@Test
	@DisplayName("포인트 사용")
	void usePoint() {
		//given
		long id = 1L;
		long amount = 220L;
		UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());
		userRepository.save(userPoint);
		
		// when
		UserPoint resultUserPoint = userPointService.use(id, 30L);
		
		// then
		assertThat(resultUserPoint.point()).isEqualTo(190L);
	}
	
	@Test
	@DisplayName("0이하 포인트 사용 실패")
	void usePointMinus() {
		//given
		long id = 1L;
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> userPointService.use(id, 0L));
	}
	
	@Test
	@DisplayName("포인트 사용 잔고 부족")
	void usePointMin() {
		//given
		long id = 1L;
		long amount = 140L;
		UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());
		userRepository.save(userPoint);
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> userPointService.use(id, 141L));
	}
	
	@Test
	@DisplayName("포인트 조회")
	void findPoint() {
		// given
		long id = 1L;
		long point = 100L;
		UserPoint userPoint = new UserPoint(id, point, System.currentTimeMillis());
		userRepository.save(userPoint);
		
		// when
		UserPoint resultUserPoint = userPointService.findPointById(id);
		
		// then
		assertThat(resultUserPoint.id()).isEqualTo(1L);
		assertThat(resultUserPoint.point()).isEqualTo(100L);
	}
	
	@Test
	@DisplayName("포인트 조회 사용자 아이디 1미만 실패")
	void findPointMinusId() {
		// given
		long id = 0L;
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> userPointService.findPointById(id));
	}
	
	@Test
	@DisplayName("포인트 충전 동시성 테스트")
	void chargeConcurrent() throws InterruptedException {
		// given
		long id = 1L;
		long amount = 100L;
		UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());
		userRepository.save(userPoint);
		
		int numberOfThread = 100;
		CountDownLatch countDownLatch = new CountDownLatch(numberOfThread);
		
		// when
		for(int i=0; i<numberOfThread; i++) {
			new Thread(() -> {
				userPointService.charge(id, 30L);                
				countDownLatch.countDown();  // 작업이 끝나면 countDown 호출
            }).start();
		}
		
		countDownLatch.await();
		
		// then
		UserPoint resultUserPoint = userPointService.findPointById(id);
		assertThat(resultUserPoint.point()).isEqualTo(3100L);
	}
	
	@Test
	@DisplayName("포인트 사용 동시성 테스트")
	void usePointConcurrent() throws InterruptedException {
		//given
		long id = 1L;
		long amount = 3000L;
		UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());
		userRepository.save(userPoint);
		
		int numberOfThread = 100;
		CountDownLatch countDownLatch = new CountDownLatch(numberOfThread);
		
		// when
		for(int i=0; i<numberOfThread; i++) {
			new Thread(() -> {
				userPointService.use(id, 20L);
				countDownLatch.countDown();
			}).start();
		}
		
		countDownLatch.await();
		
		// then
		UserPoint resultUserPoint = userPointService.findPointById(id);
		assertThat(resultUserPoint.point()).isEqualTo(1000L);
	}
}
