package io.hhplus.tdd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointHistoryRepository;
import io.hhplus.tdd.point.PointHistoryService;
import io.hhplus.tdd.point.TransactionType;

@SpringBootTest
public class PointHistoryServiceInterTest {

	@Autowired
	PointHistoryService pointHistoryService;
	
	@Autowired
	PointHistoryRepository pointHistoryRepository;
	
	@Test
	@DisplayName("포인트 히스토리 조회")
	void pointHistory() {
		// given
		long userId = 1L;
		PointHistory pointHistory1 = new PointHistory(1, userId, 100L, TransactionType.CHARGE, System.currentTimeMillis());
		PointHistory pointHistory2 = new PointHistory(2, userId, 200L, TransactionType.CHARGE, System.currentTimeMillis());
		PointHistory pointHistory3 = new PointHistory(3, userId, 250L, TransactionType.USE, System.currentTimeMillis());
		pointHistoryRepository.save(pointHistory1);
		pointHistoryRepository.save(pointHistory2);
		pointHistoryRepository.save(pointHistory3);
		
		
		// when
		List<PointHistory> pointHistories = pointHistoryService.findAllByUserId(userId);
		
		// then
		assertThat(pointHistories).hasSize(3);
		assertThat(pointHistories.get(0).amount()).isEqualTo(100L);
		assertThat(pointHistories.get(0).type()).isEqualTo(TransactionType.CHARGE); 
		assertThat(pointHistories.get(1).amount()).isEqualTo(200L);
		assertThat(pointHistories.get(1).type()).isEqualTo(TransactionType.CHARGE); 
		assertThat(pointHistories.get(2).amount()).isEqualTo(250L);
		assertThat(pointHistories.get(2).type()).isEqualTo(TransactionType.USE);
	}
	
	@Test
	@DisplayName("유저 아이디 1미만 포인트 조회 실패")
	void findPointHistoryUserIdMinus() {
		// given
		long userId = 0L;
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> pointHistoryService.findAllByUserId(userId));
	}
}
