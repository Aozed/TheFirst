package entity;

import java.io.Serializable;

/**
 * 返回页面信息的实体类
 * 注意实现序列化
 */
public class Result implements Serializable{
    private boolean success;
    private String msg;

    public Result(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public boolean isSuccess() { //怎么是is,以前没注意???
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
