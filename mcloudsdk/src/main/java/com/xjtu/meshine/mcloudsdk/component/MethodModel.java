package com.xjtu.meshine.mcloudsdk.component;

import com.xjtu.meshine.mcloudsdk.db.Table;

import java.util.UUID;

/**
 * Created by Meshine on 17/1/5.
 */

@Table(name = "t_method")
public class MethodModel {
    @Table.Column(name = "method_id",type = Table.Column.TYPE_STRING,isPrimaryKey = true)
    private String id;
    @Table.Column(name = "method_name",type = Table.Column.TYPE_STRING)
    private String method;
    @Table.Column(name = "method_class",type = Table.Column.TYPE_STRING)
    private String clss;
    @Table.Column(name = "excute_time",type = Table.Column.TYPE_LONG)
    private Long excuteTime;
    @Table.Column(name = "net_state",type = Table.Column.TYPE_INTEGER)
    private Integer netState;
    @Table.Column(name = "excute_type",type = Table.Column.TYPE_INTEGER)
    private Integer excuteType;

    public MethodModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getClss() {
        return clss;
    }

    public void setClss(String clss) {
        this.clss = clss;
    }

    public Long getExcuteTime() {
        return excuteTime;
    }

    public void setExcuteTime(Long excuteTime) {
        this.excuteTime = excuteTime;
    }

    public Integer getNetState() {
        return netState;
    }

    public void setNetState(Integer netState) {
        this.netState = netState;
    }

    public Integer getExcuteType() {
        return excuteType;
    }

    public void setExcuteType(Integer excuteType) {
        this.excuteType = excuteType;
    }

    @Override
    public String toString() {
        return "MethodModel{" +
                "id='" + id + '\'' +
                ", method='" + method + '\'' +
                ", clss='" + clss + '\'' +
                ", excuteTime=" + excuteTime +
                ", netState=" + netState +
                ", excuteType=" + excuteType +
                '}';
    }
}
