package xin.opstime.remote.dto;

/**
 * Created on 2023/5/24
 *
 * @author hergua
 */
public class Result {

    Boolean success;
    Long operationId;
    String errorMessage;
    String output;

    public Result(Boolean success, Long operationId, String errorMessage, String output) {
        this.success = success;
        this.operationId = operationId;
        this.errorMessage = errorMessage;
        this.output = output;
    }

    public Boolean getSuccess() {
        return success;
    }

    public Result setSuccess(Boolean success) {
        this.success = success;
        return this;
    }

    public Long getOperationId() {
        return operationId;
    }

    public Result setOperationId(Long operationId) {
        this.operationId = operationId;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Result setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public String getOutput() {
        return output;
    }

    public Result setOutput(String output) {
        this.output = output;
        return this;
    }
}
