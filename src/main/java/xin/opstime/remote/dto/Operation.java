package xin.opstime.remote.dto;

/**
 * Created on 2023/5/24
 *
 * @author hergua
 */
public class Operation {

    Long agent;

    String operate;

    String ip;

    Long id;

    public Long getAgent() {
        return agent;
    }

    public Operation setAgent(Long agent) {
        this.agent = agent;
        return this;
    }

    public String getOperate() {
        return operate;
    }

    public Operation setOperate(String operate) {
        this.operate = operate;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public Operation setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Long getId() {
        return id;
    }

    public Operation setId(Long id) {
        this.id = id;
        return this;
    }
}
