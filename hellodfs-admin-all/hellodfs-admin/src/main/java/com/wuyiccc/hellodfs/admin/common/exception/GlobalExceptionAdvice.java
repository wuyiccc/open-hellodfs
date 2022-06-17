package com.wuyiccc.hellodfs.admin.common.exception;

/**
 * @author wuyiccc
 * @date 2020/10/24 10:59
 * 全局异常捕获类
 */
import com.wuyiccc.hellodfs.admin.common.CommonJSONResult;
import com.wuyiccc.hellodfs.admin.common.enumeration.ResponseStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionAdvice.class);


    /**
     * 服务器未知异常拦截
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public CommonJSONResult handleException(Exception e) {
        logger.info("####exception:{}", e.toString());
        return CommonJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_UNKNOWN_ERROR);
    }

    /**
     * 拦截自定义异常
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(CustomException.class)
    public CommonJSONResult handleWantYouCustomException(CustomException e) {

        logger.error(e.getMessage());
        e.printStackTrace();
        return CommonJSONResult.exception(e.getResponseStatus());
    }

    /**
     * 文件上传超出设定大小异常捕获
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public CommonJSONResult handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return CommonJSONResult.errorCustom(ResponseStatusEnum.FILE_MAX_SIZE_ERROR);
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonJSONResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        Map<String, String> map = getErrors(result);
        return CommonJSONResult.errorMap(map);
    }

    public Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        List<FieldError> errorList = result.getFieldErrors();
        for (FieldError error : errorList) {
            // 发送验证错误的时候所对应的某个属性
            String field = error.getField();
            // 验证的错误消息
            String msg = error.getDefaultMessage();
            map.put(field, msg);
        }
        return map;
    }
}
