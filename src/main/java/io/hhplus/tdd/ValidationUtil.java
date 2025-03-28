package io.hhplus.tdd;

public class ValidationUtil {
	
	public static void validateLessZero(long value) {
		if (value <= 0) {
			throw new IllegalArgumentException("0 이하의 값을 사용할 수 없습니다.");
		}
	}
	
	public static void validateUnderOne(long value) {
        if (value < 1) {
            throw new IllegalArgumentException("1 미만의 값을 사용할 수 없습니다.");
        }
    }
	
}
