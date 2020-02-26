//package cn.com.aig.recommend.mq;
//
//import com.aig.push.model.PushUserInfo;
//import com.aig.push.model.Token;
//import com.aig.push.send.*;
//import com.aig.push.service.ConfigService;
//import com.aig.push.service.TagService;
//import com.aig.push.service.UserService;
//import com.aig.push.tag.AddTagInfoReq;
//import com.aig.push.tag.DelTagReq;
//import com.aig.push.user.*;
//import com.aig.push.util.ThreadPoolUtil;
//import com.aig.push.util.TopicUtil;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class ConsumerListener implements TopicUtil
//{
//    private static final Logger logger = LoggerFactory.getLogger(ConsumerListener.class);
//    @Autowired
//    TagService tagService;
//    @Autowired
//    UserService userService;
//    @Autowired
//    ConfigService configService;
//    @Autowired
//    private RedisTemplate redisTemplate;
//    /**
//     * 添加tag
//     * @param consumerRecord
//     */
//    @KafkaListener(groupId = "pushGroup",topics = {ADD_TAG_MQ},concurrency = "5")
//    public void addTag(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack)
//    {
//        logger.info("添加 tag: kafka msg " +consumerRecord.value());
//        try {
//            AddTagInfoReq req =JSONObject.parseObject(consumerRecord.value(), AddTagInfoReq.class);
//            tagService.saveFollow(req);
//        }
//        catch (Exception e)
//        {
//            logger.error("delTag:",e.getMessage(),e);
//        }
//        finally {
//            ack.acknowledge();
//        }
//    }
//
//    /**
//     * 删除 tag
//     * @param consumerRecord
//     */
//    @KafkaListener(groupId = "pushGroup",topics = {DEL_TAG_MQ},concurrency = "5")
//    public void delTag(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack)
//    {
//        logger.info("DelTagMq kafka msg " +consumerRecord.value());
//        try {
//            DelTagReq req =JSONObject.parseObject(consumerRecord.value(), DelTagReq.class);
//            tagService.delFollow(req);
//        }
//        catch (Exception e )
//        {
//            logger.error("delTag:",e.getMessage(),e);
//        }
//        finally {
//            ack.acknowledge();
//        }
//    }
//
//
//
//
//    /**
//     * 单发无模板
//     * @param consumerRecord
//     */
//    @KafkaListener(groupId = "pushGroup",topics = {SINGLE_SEND_MQ_NO_TEMP},concurrency = "5")
//    public void singleSendNoTemp(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack)
//    {
//        logger.info("SingleSendMqNoTemp kafka msg" +consumerRecord.value());
//        try {
//            SingleSendNoTempReq req =JSONObject.parseObject(consumerRecord.value(), SingleSendNoTempReq.class);
//            tagService.singleSendNoTemp(req);
//        }
//        catch (Exception e)
//        {
//            logger.error("singleSendNoTemp:",e.getMessage(),e);
//        }
//        finally {
//            ack.acknowledge();
//        }
//    }
//
//    /**
//     * 批量消息推送（IM)
//     * @param consumerRecord
//     */
//    @KafkaListener(groupId = "pushGroup",topics = {BATCH_USER_DATA_PUSH},concurrency = "5")
//    public void batchUserDataPush(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack)
//    {
//        logger.info("BatchUserDataPush kafka msg" +consumerRecord.value());
//        try {
//
//        BatchUserDataPushReq   req = JSON.toJavaObject(JSON.parseObject(consumerRecord.value()),  BatchUserDataPushReq.class);
//          tagService.batchUserDataPush(req);
//        }
//        catch (Exception e)
//        {
//            logger.error("pushGroup: " +consumerRecord.value() +"   " ,e.getMessage(),e);
//        }
//        finally {
//            ack.acknowledge();
//        }
//
//    }
//
//    /**
//     * 添加更新用户
//     * @param consumerRecord
//     */
//    @KafkaListener(groupId = "pushGroup",topics = {USER_DATA_MQ},concurrency = "5")
//    public void saveUpdateUser(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack)
//    {
//        logger.info("saveUpdateUser kafka msg" +consumerRecord.value());
//        try
//        {
//            UserInfoAddReq req =JSONObject.parseObject(consumerRecord.value(), UserInfoAddReq.class);
//            String k = configService.getConf(req.getAppId()).getSyncData();
//            redisTemplate.opsForZSet().add(k,req.getUid(),System.currentTimeMillis());
//            String tableName = configService.getConf(req.getAppId()).getTableName();
//            PushUserInfo info = getPushUserInfo(req);
//            info.setUpdateTime(req.getTimeStamp());
//            userService.saveUpdateUser(info,tableName);
//        }
//        catch (Exception e)
//        {
//            logger.error("saveUpdateUser:",e.getMessage(),e);
//        }
//        finally {
//            ack.acknowledge();
//        }
//
//    }
//    /**
//     * 批量添加用户
//     * @param consumerRecord
//     */
//    @KafkaListener(groupId = "pushGroup",topics = {USER_BATCH_DATA_ADD_MQ},concurrency = "5")
//    public void batchUserInfoAdd(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack)
//    {
//        logger.info("UserBatchAdd kafka msg" +consumerRecord.value());
//        try {
//            BatchUserInfoAddReq req =JSONObject.parseObject(consumerRecord.value(), BatchUserInfoAddReq.class);
//            String tableName = configService.getConf(req.getAppId()).getTableName();
//            List<UserInfo> list =  req.getUserInfos();
//            for(UserInfo userInfo :list)
//            {
//                PushUserInfo info = new PushUserInfo();
//                BeanUtils.copyProperties(userInfo,info);
//                info.setLocation(JSON.toJSONString(userInfo.getLocation()));
//                Map<String, PushChannal>   mp =  userInfo.getPushChannals();
//                Map<String, PushChannal> maps  =new HashMap<>();
//                for (String key : mp.keySet())
//                {
//                    PushChannal channal = mp.get(key);
//                    maps.put(channal.getDeviceType()+"_"+channal.getPushChannal(),channal);
//                }
//                info.setPushChannals(JSON.toJSONString(maps));
//                info.setSetting(JSON.toJSONString(userInfo.getSetting()));
//                info.setLocation(JSON.toJSONString(userInfo.getLocation()));
//                info.setUpdateTime(req.getTimeStamp());
//                userService.saveUpdateUser(info,tableName);
//            }
//        }
//        catch (Exception e)
//        {
//            logger.error("batchUserInfoAdd:",e.getMessage(),e);
//        }
//        finally {
//            ack.acknowledge();
//        }
//    }
//
//    private PushUserInfo getPushUserInfo(UserInfoAddReq req) {
//        PushUserInfo info = new PushUserInfo();
//        BeanUtils.copyProperties(req, info);
//        Map<String, PushChannal> mp = req.getPushChannals();
//        Map<String, PushChannal> maps = new HashMap<>();
//        if (req.getPushChannals() != null)
//        {
//            for (String key : mp.keySet()) {
//                PushChannal channal = mp.get(key);
//                maps.put(channal.getDeviceType() + "_" + channal.getPushChannal(), channal);
//            }
//            info.setPushChannals(JSON.toJSONString(maps));
//        }
//        if (req.getSetting() != null )
//            info.setSetting(JSON.toJSONString(req.getSetting()));
//
//        if (req.getLocation() != null )
//            info.setLocation(JSON.toJSONString(req.getLocation()));
//        return info;
//    }
//
//
//    /**
//     * tag  消息 转发
//     * @param consumerRecord
//     */
//    @KafkaListener(groupId = "pushGroup",topics = {SEND_BY_TAG_MQ},concurrency = "5")
//    public void sendByTag(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack)
//    {
//        logger.info("SendByTagMq kafka msg " +consumerRecord.value());
//        try {
//            PushReq req =JSONObject.parseObject(consumerRecord.value(), PushReq.class);
//            tagService.sendByTag(req);
//        }
//        catch (Exception e)
//        {
//            logger.error("sendByTag:",e.getMessage(),e);
//        }
//        finally {
//            ack.acknowledge();
//        }
//    }
//
//    /**
//     * tag 消息通推送
//     * @param consumerRecord
//     */
//    @KafkaListener(groupId = "pushGroup",topics = {SEND_BY_TAG_MQ_RESEND},concurrency = "5")
//    public void sendByTagResend(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack)
//    {
//        logger.info("SendByTagMqResend kafka msg" +consumerRecord.value());
//        try {
//
//            JSONObject json = JSONObject.parseObject(consumerRecord.value());
//            PushReq req  = JSONObject.parseObject(String.valueOf(json.get("req")), PushReq.class);
//            List<Long> list  = JSON.parseArray(String.valueOf(json.get("follows")), Long.class);
//            if(list.size()>0)
//            ThreadPoolUtil.executor(()-> this.tagService.sendByTagSecond(req,list));
//        }
//        catch (Exception e)
//        {
//            logger.error("sendByTagResend:",e.getMessage(),e);
//        }
//        finally {
//            ack.acknowledge();
//        }
//    }
//
//    /**
//     * 群发 消息转发
//     * @param consumerRecord
//     */
//    @KafkaListener(groupId = "pushGroup",topics = {GROUP_SEND_MQ},concurrency = "5")
//    public void groupSend(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack)
//    {
//        logger.info("GroupSendMq kafka msg " +consumerRecord.value());
//        try {
//            GroupSendReq req =JSONObject.parseObject(consumerRecord.value(), GroupSendReq.class);
//            ThreadPoolUtil.executor(()-> this.tagService.groupSend(req));
//        }
//        catch (Exception e)
//        {
//            logger.error("groupSend:",e.getMessage(),e);
//        }
//        finally {
//            ack.acknowledge();
//        }
//    }
//
//    /**
//     * 群发消息推送
//     * @param consumerRecord
//     */
//    @KafkaListener(groupId = "pushGroup",topics = {GROUP_SEND_MQ_RESEND},concurrency = "10")
//    public void sendGroupResend(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack)
//    {
//        logger.info("GroupSendMqResend kafka msg" +consumerRecord.value());
//        try {
//            JSONObject json = JSONObject.parseObject(consumerRecord.value());
//            List<Long> list  = JSON.parseArray(String.valueOf(json.get("follows")), Long.class);
//            GroupSendReq  req =  JSONObject.parseObject(String.valueOf(json.get("req")), GroupSendReq.class);
//            ThreadPoolUtil.executor(()-> this.tagService.groupSenDSecond(req,list));
//        }
//        catch (Exception e)
//        {
//            logger.error("sendGroupResend:",e.getMessage(),e);
//        }
//        finally {
//            ack.acknowledge();
//        }
//    }
//
//
//
//    /**
//     * 单发需要模板
//     * @param consumerRecord
//     */
//    @KafkaListener(groupId = "pushGroup",topics = {SINGLE_SEND_MQ},concurrency = "5")
//    public void singleSend(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack)
//    {
//        logger.info("SingleSendMq kafka msg" +consumerRecord.value());
//        try {
//            SingleSendReq req =JSONObject.parseObject(consumerRecord.value(), SingleSendReq.class);
//            tagService.singleSend(req);
//        }
//        catch (Exception e)
//        {
//            logger.error("singleSend:",e.getMessage(),e);
//        }
//        finally {
//            ack.acknowledge();
//        }
//    }
//
//    /**
//     * 群发消息推送
//     * @param consumerRecord
//     */
//    @KafkaListener(groupId = "pushGroup",topics = {USER_BADGE_MQ},concurrency = "5")
//    public void updateBadge(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack)
//    {
//        logger.info("UserBadgeMq kafka msg" +consumerRecord.value());
//        try {
//            UserBadegeReq req = JSONObject.parseObject(consumerRecord.value(), UserBadegeReq.class);
//            String tableName = configService.getConf(req.getAppId()).getTableName();
//            userService.updateBadage(req.getUid(),tableName,req.getBadge());
//        }
//        catch (Exception e)
//        {
//            logger.error("updateBadge:",e.getMessage(),e);
//        }
//        finally {
//            ack.acknowledge();
//        }
//    }
//
//
//    /**
//     * 群发消息推送
//     * @param consumerRecord
//     */
//    @KafkaListener(groupId = "pushGroup",topics = {PUSH_TOKEN_DEL},concurrency = "5")
//    public void delToken(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack)
//    {
//        logger.info("delToken kafka msg" +consumerRecord.value());
//        Token token = JSONObject.parseObject(consumerRecord.value(), Token.class);
//        try {
//            String tableName = configService.getConf(Integer.valueOf(token.getAppId())).getTableName();
//            userService.updateToken("update   " +
//                    tableName +
//                    " set push_channals =push_channals::jsonb - '" +
//                    token.getDeviceType() +
//                    "_" +
//                    token.getPushChannel() +
//                    "'  where uid=" +
//                    Long.valueOf(token.getUid()));
//        }
//        catch (Exception e)
//        {
//            logger.error("delToken:",e.getMessage(),e);
//        }
//        finally {
//            ack.acknowledge();
//        }
//    }
//
//
//
//    /**
//     * 批量消息推送（IM)
//     * @param consumerRecord
//     */
//    @KafkaListener(groupId = "pushGroup",topics = {MUTIL_UID_PUSH},concurrency = "1")
//    public void pushByMuilUid(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack)
//    {
//        logger.info("PushByMutilUidReq kafka msg" +consumerRecord.value());
//        try {
//            PushByMutilUidReq   req = JSON.toJavaObject(JSON.parseObject(consumerRecord.value()),  PushByMutilUidReq.class);
//            tagService.pushByMutilUid(req);
//        }
//        catch (Exception e)
//        {
//            logger.error("PushByMutilUidReq: " +consumerRecord.value() +"   " ,e.getMessage(),e);
//        }
//        finally {
//            ack.acknowledge();
//        }
//
//    }
//}