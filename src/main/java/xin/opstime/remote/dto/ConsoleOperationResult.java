package xin.opstime.remote.dto;

/**
 * Created on 2023/5/25
 *
 * @author hergua
 */
public class ConsoleOperationResult {

    String msg;

    Boolean success;

    public ConsoleOperationResult(String msg, Boolean success) {
        this.msg = msg;
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public ConsoleOperationResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Boolean getSuccess() {
        return success;
    }

    public ConsoleOperationResult setSuccess(Boolean success) {
        this.success = success;
        return this;
    }

    public ConsoleOperationResult() {
    }
}
