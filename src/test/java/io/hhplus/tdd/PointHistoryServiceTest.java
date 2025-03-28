package io.hhplus.tdd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointHistoryRepository;
import io.hhplus.tdd.point.PointHistoryServiceImpl;
import io.hhplus.tdd.point.TransactionType;

public class PointHistoryServiceTest {

	@Mock
	PointHistoryRepository pointHistoryRepository;
	
	@InjectMocks
	PointHistoryServiceImpl pointHistoryService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);  // Mockito 초기화
	}
	
	@Test
	@DisplayName("포인트 내역 조회")
	void findPointHistory() {
		// given
		long userId = 1L;
		
		PointHistory pointHistory1 = new PointHistory(1, userId, 100L, TransactionType.CHARGE, System.currentTimeMillis());
		PointHistory pointHistory2 = new PointHistory(2, userId, 200L, TransactionType.CHARGE, System.currentTimeMillis());
		PointHistory pointHistory3 = new PointHistory(3, userId, 250L, TransactionType.USE, System.currentTimeMillis());
		
		when(pointHistoryRepository.findAllByUserId(userId)).thenReturn(Arrays.asList(pointHistory1, pointHistory2, pointHistory3));
		
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
