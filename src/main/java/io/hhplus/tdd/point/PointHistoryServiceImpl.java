package io.hhplus.tdd.point;

import java.util.List;

import org.springframework.stereotype.Service;

import io.hhplus.tdd.ValidationUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService{

	private final PointHistoryRepository pointHistoryRepository;
	
	@Override
	public List<PointHistory> findAllByUserId(long userId) {
		ValidationUtil.validateUnderOne(userId);
		return pointHistoryRepository.findAllByUserId(userId);
	}
}
