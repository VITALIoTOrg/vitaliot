
package jsonpojos;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "activeSessions",
    "currInternalSessions",
    "currRemoteSessions",
    "cumPolicyEval",
    "avgPolicyEval",
    "avgPolicyEvalTree"
})
public class Monitor {

    @JsonProperty("activeSessions")
    private long activeSessions;
    @JsonProperty("currInternalSessions")
    private long currInternalSessions;
    @JsonProperty("currRemoteSessions")
    private long currRemoteSessions;
    @JsonProperty("cumPolicyEval")
    private long cumPolicyEval;
    @JsonProperty("avgPolicyEval")
    private long avgPolicyEval;
    @JsonProperty("avgPolicyEvalTree")
    private long avgPolicyEvalTree;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The activeSessions
     */
    @JsonProperty("activeSessions")
    public long getActiveSessions() {
        return activeSessions;
    }

    /**
     * 
     * @param activeSessions
     *     The activeSessions
     */
    @JsonProperty("activeSessions")
    public void setActiveSessions(long activeSessions) {
        this.activeSessions = activeSessions;
    }

    public Monitor withActiveSessions(long activeSessions) {
        this.activeSessions = activeSessions;
        return this;
    }

    /**
     * 
     * @return
     *     The currInternalSessions
     */
    @JsonProperty("currInternalSessions")
    public long getCurrInternalSessions() {
        return currInternalSessions;
    }

    /**
     * 
     * @param currInternalSessions
     *     The currInternalSessions
     */
    @JsonProperty("currInternalSessions")
    public void setCurrInternalSessions(long currInternalSessions) {
        this.currInternalSessions = currInternalSessions;
    }

    public Monitor withCurrInternalSessions(long currInternalSessions) {
        this.currInternalSessions = currInternalSessions;
        return this;
    }
    
    /**
     * 
     * @return
     *     The currRemoteSessions
     */
    @JsonProperty("currRemoteSessions")
    public long getCurrRemoteSessions() {
        return currRemoteSessions;
    }

    /**
     * 
     * @param currRemoteSessions
     *     The currRemoteSessions
     */
    @JsonProperty("currRemoteSessions")
    public void setCurrRemoteSessions(long currRemoteSessions) {
        this.currRemoteSessions = currRemoteSessions;
    }

    public Monitor withCurrRemoteSessions(long currRemoteSessions) {
        this.currRemoteSessions = currRemoteSessions;
        return this;
    }

    /**
     * 
     * @return
     *     The cumPolicyEval
     */
    @JsonProperty("cumPolicyEval")
    public long getCumPolicyEval() {
        return cumPolicyEval;
    }

    /**
     * 
     * @param cumPolicyEval
     *     The cumPolicyEval
     */
    @JsonProperty("cumPolicyEval")
    public void setCumPolicyEval(long cumPolicyEval) {
        this.cumPolicyEval = cumPolicyEval;
    }

    public Monitor withCumPolicyEval(long cumPolicyEval) {
        this.cumPolicyEval = cumPolicyEval;
        return this;
    }

    /**
     * 
     * @return
     *     The avgPolicyEval
     */
    @JsonProperty("avgPolicyEval")
    public long getAvgPolicyEval() {
        return avgPolicyEval;
    }

    /**
     * 
     * @param avgPolicyEval
     *     The avgPolicyEval
     */
    @JsonProperty("avgPolicyEval")
    public void setAvgPolicyEval(long avgPolicyEval) {
        this.avgPolicyEval = avgPolicyEval;
    }

    public Monitor withAvgPolicyEval(long avgPolicyEval) {
        this.avgPolicyEval = avgPolicyEval;
        return this;
    }

    /**
     * 
     * @return
     *     The avgPolicyEvalTree
     */
    @JsonProperty("avgPolicyEvalTree")
    public long getAvgPolicyEvalTree() {
        return avgPolicyEvalTree;
    }

    /**
     * 
     * @param avgPolicyEvalTree
     *     The avgPolicyEvalTree
     */
    @JsonProperty("avgPolicyEvalTree")
    public void setAvgPolicyEvalTree(long avgPolicyEvalTree) {
        this.avgPolicyEvalTree = avgPolicyEvalTree;
    }

    public Monitor withAvgPolicyEvalTree(long avgPolicyEvalTree) {
        this.avgPolicyEvalTree = avgPolicyEvalTree;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Monitor withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
