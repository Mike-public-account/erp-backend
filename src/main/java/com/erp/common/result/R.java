package com.erp.common.result;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class R<T> {
    private Integer code;
    private String msg;
    private T data;

    // 成功 带数据
    public static <T> R<T> ok(T data) {
        return new R<T>().setCode(200).setMsg("操作成功").setData(data);
    }

    // 成功 无数据
    public static <T> R<T> ok() {
        return new R<T>().setCode(200).setMsg("操作成功");
    }

    // 失败 自定义码+信息
    public static <T> R<T> fail(Integer code, String msg) {
        return new R<T>().setCode(code).setMsg(msg);
    }

    // 失败 仅信息
    public static <T> R<T> fail(String msg) {
        return new R<T>().setCode(500).setMsg(msg);
    }
}
