package io.hhplus.tdd.point;

import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Service;

import io.hhplus.tdd.ValidationUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPointServiceImpl implements UserPointService{
	
	private final UserPointRepository userPointRepository;
	private final PointHistoryRepository pointHistoryRepository;
	private final ReentrantLock lock = new ReentrantLock();

	@Override
	public UserPoint charge(long id, long amount) {
		ValidationUtil.validateLessZero(amount);
		lock.lock();
		
		// try-finally 없는 경우 예외발생시 데드락 상태가 될 수 있음. finally로 반드시 unlock!!
		try {
			UserPoint userPoint = userPointRepository.findById(id);
			userPoint = userPoint.charge(amount);
			userPointRepository.save(userPoint);
			
			PointHistory pointHistory = new PointHistory(0, id, amount, TransactionType.CHARGE, System.currentTimeMillis());
			pointHistoryRepository.save(pointHistory);
			
			return userPoint;
		}finally {
			lock.unlock();
		}
	}

	@Override
	public UserPoint use(long id, long amount) {
		ValidationUtil.validateLessZero(amount);
		lock.lock();
		try {
			UserPoint userPoint = userPointRepository.findById(id);
			userPoint = userPoint.use(amount);
			userPointRepository.save(userPoint);
			
			PointHistory pointHistory = new PointHistory(0, id, amount, TransactionType.USE, System.currentTimeMillis());
			pointHistoryRepository.save(pointHistory);
			
			return userPoint;
		}finally {
			lock.unlock();
		}
	}

	@Override
	public UserPoint findPointById(long id) {
		ValidationUtil.validateUnderOne(id);
		return userPointRepository.findById(id);
	}
}
