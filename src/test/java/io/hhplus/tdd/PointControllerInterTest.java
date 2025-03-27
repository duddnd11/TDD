package io.hhplus.tdd;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import io.hhplus.tdd.point.PointHistoryService;
import io.hhplus.tdd.point.UserPointService;

@SpringBootTest
@AutoConfigureMockMvc
public class PointControllerInterTest {
	
	@Autowired
    MockMvc mockMvc;
	
	@Autowired
	UserPointService userPointService;
	
	@Autowired
	PointHistoryService pointHistoryService;
	
	@Test
	@DirtiesContext // 테스트 끝난후 컨텍스트 초기화
	@DisplayName("포인트 조회 컨트롤러 테스트")
	void point() throws Exception {
		long id = 1L;
		long amount = 200L;
		userPointService.charge(id, amount);
		
		// get 요청
        mockMvc.perform(get("/point/1"))
               .andExpect(status().isOk())  // 상태 코드 200이 반환되는지 확인
               .andExpect(jsonPath("$.id").value(1L))
               .andExpect(jsonPath("$.point").value(200L));
               
	}
	
	@Test
	@DirtiesContext
	@DisplayName("포인트 내역 조회 컨트롤러 테스트")
	void pointHistories() throws Exception {
		long id = 1L;
		
		userPointService.charge(id, 200L);
		userPointService.charge(id, 300L);
		userPointService.use(id, 500L);
		
		pointHistoryService.findAllByUserId(id);
		
		mockMvc.perform(get("/point/1/histories"))
				.andExpect(status().isOk())  // 상태 코드 200이 반환되는지 확인
				.andExpect(jsonPath("$[0].userId").value(1L))
				.andExpect(jsonPath("$[0].amount").value(200L))
				.andExpect(jsonPath("$[0].type").value("CHARGE"))
				.andExpect(jsonPath("$[1].userId").value(1L))
				.andExpect(jsonPath("$[1].amount").value(300L))
				.andExpect(jsonPath("$[1].type").value("CHARGE"))
				.andExpect(jsonPath("$[2].userId").value(1L))
				.andExpect(jsonPath("$[2].amount").value(500L))
				.andExpect(jsonPath("$[2].type").value("USE"));
	}
	
	@Test
	@DirtiesContext
	@DisplayName("포인트 충전 컨트롤러 테스트")
	void pointCharge() throws Exception {
		mockMvc.perform(patch("/point/1/charge")  // PATCH 요청을 "/point/1" URL로 보냄
			       .contentType(MediaType.APPLICATION_JSON)  // 요청 콘텐츠 타입을 JSON으로 설정
			       .content("500"))  // 요청 본문에 수정할 데이터 제공
			       .andExpect(status().isOk())  // 상태 코드가 200인지 확인
			       .andExpect(jsonPath("$.point").value(500));  // 반환된 JSON에서 points 값이 500인지 확인
	}
	
	@Test
	@DirtiesContext
	@DisplayName("포인트 사용 컨트롤러 테스트")
	void pointUse() throws Exception {
		userPointService.charge(1L, 500L);
		
		mockMvc.perform(patch("/point/1/use")  // PATCH 요청을 "/point/1" URL로 보냄
			       .contentType(MediaType.APPLICATION_JSON)  // 요청 콘텐츠 타입을 JSON으로 설정
			       .content("300"))  // 요청 본문에 수정할 데이터 제공
			       .andExpect(status().isOk())  // 상태 코드가 200인지 확인
			       .andExpect(jsonPath("$.point").value(200));  // 반환된 JSON에서 points 값이 500인지 확인
	}
}
