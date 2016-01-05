
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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "currentpassword",
    "userpassword"
})
public class ChangePasswordRequest {

    @JsonProperty("currentpassword")
    private String currentpassword;
    @JsonProperty("userpassword")
    private String userpassword;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The currentpassword
     */
    @JsonProperty("currentpassword")
    public String getCurrentpassword() {
        return currentpassword;
    }

    /**
     * 
     * @param currentpassword
     *     The currentpassword
     */
    @JsonProperty("currentpassword")
    public void setCurrentpassword(String currentpassword) {
        this.currentpassword = currentpassword;
    }

    public ChangePasswordRequest withCurrentpassword(String currentpassword) {
        this.currentpassword = currentpassword;
        return this;
    }
    
    /**
     * 
     * @return
     *     The userpassword
     */
    @JsonProperty("userpassword")
    public String getUserpassword() {
        return userpassword;
    }

    /**
     * 
     * @param userpassword
     *     The userpassword
     */
    @JsonProperty("userpassword")
    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public ChangePasswordRequest withUserpassword(String userpassword) {
        this.userpassword = userpassword;
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

    public ChangePasswordRequest withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
