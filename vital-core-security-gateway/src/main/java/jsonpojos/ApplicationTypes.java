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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "result",
    "resultCount",
    "pagedResultsCookie",
    "remainingPagedResults"
})
public class ApplicationTypes {

    @JsonProperty("result")
    private List<ApplicationType> result = new ArrayList<ApplicationType>();
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
    public List<ApplicationType> getResult() {
        return result;
    }

    /**
     * 
     * @param result
     *     The result
     */
    @JsonProperty("result")
    public void setResult(List<ApplicationType> result) {
        this.result = result;
    }

    public ApplicationTypes withResult(List<ApplicationType> result) {
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

    public ApplicationTypes withResultCount(Integer resultCount) {
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

    public ApplicationTypes withPagedResultsCookie(Object pagedResultsCookie) {
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

    public ApplicationTypes withRemainingPagedResults(Integer remainingPagedResults) {
        this.remainingPagedResults = remainingPagedResults;
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

    public ApplicationTypes withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
