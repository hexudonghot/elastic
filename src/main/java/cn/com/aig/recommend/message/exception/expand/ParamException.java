package cn.com.aig.recommend.message.exception.expand;


import cn.com.aig.recommend.message.enums.ResponseCode;
import cn.com.aig.recommend.message.exception.GlobalException;

public class ParamException extends GlobalException {

    public ParamException(String message){
        super(message, ResponseCode.PARAM_ERROR_CODE.getCode());
    }
}
