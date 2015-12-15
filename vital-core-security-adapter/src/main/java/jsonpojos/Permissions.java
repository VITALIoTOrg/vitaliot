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
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "allowed",
    "denied"
})
public class Permissions {

    @JsonProperty("allowed")
    private List<String> allowed = new ArrayList<String>();
    @JsonProperty("denied")
    private List<String> denied = new ArrayList<String>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The allowed
     */
    @JsonProperty("allowed")
    public List<String> getAllowed() {
        return allowed;
    }

    /**
     * 
     * @param allowed
     *     The allowed
     */
    @JsonProperty("allowed")
    public void setAllowed(List<String> allowed) {
        this.allowed = allowed;
    }

    public Permissions withAllowed(List<String> allowed) {
        this.allowed = allowed;
        return this;
    }
    
    /**
     * 
     * @return
     *     The denied
     */
    @JsonProperty("denied")
    public List<String> getDenied() {
        return denied;
    }

    /**
     * 
     * @param denied
     *     The denied
     */
    @JsonProperty("denied")
    public void setDenied(List<String> denied) {
        this.denied = denied;
    }

    public Permissions withDenied(List<String> denied) {
        this.denied = denied;
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

    public Permissions withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
