package io.hhplus.tdd.point;

import org.springframework.stereotype.Service;

import io.hhplus.tdd.ValidationUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPointServiceImpl implements UserPointService{
	
	private final UserPointRepository userPointRepository;
	private final PointHistoryRepository pointHistoryRepository;

	@Override
	public UserPoint charge(long id, long amount) {
		ValidationUtil.validateLessZero(amount);
		
		UserPoint userPoint = userPointRepository.findById(id);
		userPoint = userPoint.charge(amount);
		userPointRepository.save(userPoint);
		
		PointHistory pointHistory = new PointHistory(0, id, amount, TransactionType.CHARGE, System.currentTimeMillis());
		pointHistoryRepository.save(pointHistory);
		return userPoint;
	}

	@Override
	public UserPoint use(long id, long amount) {
		ValidationUtil.validateLessZero(amount);
		
		UserPoint userPoint = userPointRepository.findById(id);
		userPoint = userPoint.use(amount);
		userPointRepository.save(userPoint);
		
		PointHistory pointHistory = new PointHistory(0, id, amount, TransactionType.USE, System.currentTimeMillis());
		pointHistoryRepository.save(pointHistory);
		return userPoint;
	}

	@Override
	public UserPoint findPointById(long id) {
		ValidationUtil.validateUnderOne(id);
		return userPointRepository.findById(id);
	}
}
