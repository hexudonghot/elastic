package cn.com.aig.recommend.entiy;

import cn.com.aig.recommend.config.Global;
import cn.com.aig.recommend.stas.Cons;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public abstract class BaseEntity<ID extends Serializable> extends IdEntity<ID> {

    @JSONField
    private String dtype;

    public String getDtype() {
        return Global.getConfig(Cons.DB_TYPE);
    }


}
