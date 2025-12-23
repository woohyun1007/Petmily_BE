package kwh.Petmily_BE.global.error.exception;

import kwh.Petmily_BE.global.error.ErrorCode;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
