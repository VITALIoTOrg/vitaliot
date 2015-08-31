
package jsonpojos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    "result",
    "resultCount",
    "pagedResultsCookie",
    "remainingPagedResults"
})
public class Users {

    @JsonProperty("result")
    private List<String> result = new ArrayList<String>();
    @JsonProperty("resultCount")
    private Integer resultCount;
    @JsonProperty("pagedResultsCookie")
    private Object pagedResultsCookie;
    @JsonProperty("remainingPagedResults")
    private Integer remainingPagedResults;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The result
     */
    @JsonProperty("result")
    public List<String> getResult() {
        return result;
    }

    /**
     * 
     * @param result
     *     The result
     */
    @JsonProperty("result")
    public void setResult(List<String> result) {
        this.result = result;
    }

    public Users withResult(List<String> result) {
        this.result = result;
        return this;
    }

    /**
     * 
     * @return
     *     The resultCount
     */
    @JsonProperty("resultCount")
    public Integer getResultCount() {
        return resultCount;
    }

    /**
     * 
     * @param resultCount
     *     The resultCount
     */
    @JsonProperty("resultCount")
    public void setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
    }

    public Users withResultCount(Integer resultCount) {
        this.resultCount = resultCount;
        return this;
    }

    /**
     * 
     * @return
     *     The pagedResultsCookie
     */
    @JsonProperty("pagedResultsCookie")
    public Object getPagedResultsCookie() {
        return pagedResultsCookie;
    }

    /**
     * 
     * @param pagedResultsCookie
     *     The pagedResultsCookie
     */
    @JsonProperty("pagedResultsCookie")
    public void setPagedResultsCookie(Object pagedResultsCookie) {
        this.pagedResultsCookie = pagedResultsCookie;
    }

    public Users withPagedResultsCookie(Object pagedResultsCookie) {
        this.pagedResultsCookie = pagedResultsCookie;
        return this;
    }

    /**
     * 
     * @return
     *     The remainingPagedResults
     */
    @JsonProperty("remainingPagedResults")
    public Integer getRemainingPagedResults() {
        return remainingPagedResults;
    }

    /**
     * 
     * @param remainingPagedResults
     *     The remainingPagedResults
     */
    @JsonProperty("remainingPagedResults")
    public void setRemainingPagedResults(Integer remainingPagedResults) {
        this.remainingPagedResults = remainingPagedResults;
    }

    public Users withRemainingPagedResults(Integer remainingPagedResults) {
        this.remainingPagedResults = remainingPagedResults;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Users withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(result).append(resultCount).append(pagedResultsCookie).append(remainingPagedResults).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Users) == false) {
            return false;
        }
        Users rhs = ((Users) other);
        return new EqualsBuilder().append(result, rhs.result).append(resultCount, rhs.resultCount).append(pagedResultsCookie, rhs.pagedResultsCookie).append(remainingPagedResults, rhs.remainingPagedResults).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
