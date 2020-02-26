package cn.com.aig.recommend.palant.controller;

import cn.com.aig.recommend.message.enums.ResponseCode;
import cn.com.aig.recommend.palant.vo.IdxVo;
import cn.com.aig.recommend.response.ResponseResult;
import cn.com.aig.recommend.service.BaseElasticService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName ElasticIndexController
 * @Description ElasticSearch索引的基本管理，提供对外查询、删除和新增功能
 * @Version 1.0.0
*/
@Slf4j
@RequestMapping("/elastic")
@RestController
public class ElasticIndexController {

    @Autowired
    BaseElasticService baseElasticService;

    @GetMapping(value = "/")
    public ResponseResult index(String index){
        return new ResponseResult();
    }

    /**
     * @Description 创建Elastic索引
     * @param idxVo
     * @return  ResponseResult
     * @throws
     *   {"idxName":"idx_locat","idxSql":{"dynamic":false,"properties":{"id":{"type":"long"},"flag":{"type":"text","index":true},"localCode":{"type":"text","index":true},"localName":{"type":"text","index":true},"lv":{"type":"long"},"supLocalCode":{"type":"text","index":true},"url":{"type":"text","index":true}}}}
     */
    @PostMapping(value = "/createIndex")
    public ResponseResult createIndex(@RequestBody IdxVo idxVo){
        ResponseResult response = new ResponseResult();
        try {
            //索引不存在，再创建，否则不允许创建
            if(!baseElasticService.isExistsIndex(idxVo.getIdxName())){
                String idxSql = JSON.toJSONString(idxVo.getIdxSql());
                log.warn(" idxName={}, idxSql={}",idxVo.getIdxName(),idxSql);
                baseElasticService.createIndex(idxVo.getIdxName(),idxSql);
            } else{
                response.setStatus(false);
                response.setCode(ResponseCode.DUPLICATEKEY_ERROR_CODE.getCode());
                response.setMsg("索引已经存在，不允许创建");
            }
        } catch (Exception e) {
            response.setStatus(false);
            response.setCode(ResponseCode.ERROR.getCode());
            response.setMsg(ResponseCode.ERROR.getMsg());
            e.printStackTrace();
        }
        return response;
    }


    /**
     * @Description 判断索引是否存在；存在-TRUE，否则-FALSE
     * @param index
     * @return  ResponseResult
     * @throws
     */
    @GetMapping(value = "/exist/{index}")
    public ResponseResult indexExist(@PathVariable(value = "index") String index){

        ResponseResult response = new ResponseResult();
        try {
            if(!baseElasticService.isExistsIndex(index)){
                log.error("index={},不存在",index);
                response.setCode(ResponseCode.RESOURCE_NOT_EXIST.getCode());
                response.setMsg(ResponseCode.RESOURCE_NOT_EXIST.getMsg());
            } else {
                response.setMsg(" 索引已经存在, " + index);
            }
        } catch (Exception e) {
            response.setCode(ResponseCode.NETWORK_ERROR.getCode());
            response.setMsg(" 调用ElasticSearch 失败！");
            response.setStatus(false);
        }
        return response;
    }

    @GetMapping(value = "/del/{index}")
    public ResponseResult indexDel(@PathVariable(value = "index") String index){
        ResponseResult response = new ResponseResult();
        try {
            baseElasticService.deleteIndex(index);
        } catch (Exception e) {
            response.setCode(ResponseCode.NETWORK_ERROR.getCode());
            response.setMsg(" 调用ElasticSearch 失败！");
            response.setStatus(false);
        }
        return response;
    }
}
