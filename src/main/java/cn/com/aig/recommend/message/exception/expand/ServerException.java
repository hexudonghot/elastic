package cn.com.aig.recommend.message.exception.expand;


import cn.com.aig.recommend.message.enums.ResponseCode;
import cn.com.aig.recommend.message.exception.GlobalException;

public class ServerException extends GlobalException {

    public ServerException(String message){
        super(message, ResponseCode.ERROR.getCode());
    }
}
