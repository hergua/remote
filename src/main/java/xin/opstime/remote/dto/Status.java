package xin.opstime.remote.dto;

/**
 * Created on 2023/5/24
 *
 * @author hergua
 */
public class Status {

    String statusDescr;

    Long agent;

    public Status(String statusDescr, Long agent) {
        this.statusDescr = statusDescr;
        this.agent = agent;
    }

    public String getStatusDescr() {
        return statusDescr;
    }

    public Status setStatusDescr(String statusDescr) {
        this.statusDescr = statusDescr;
        return this;
    }

    public Long getAgent() {
        return agent;
    }

    public Status setAgent(Long agent) {
        this.agent = agent;
        return this;
    }
}
