
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
    "tokenId",
    "successUrl"
})
public class Authenticate {

    @JsonProperty("tokenId")
    private String tokenId;
    @JsonProperty("successUrl")
    private String successUrl;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The tokenId
     */
    @JsonProperty("tokenId")
    public String getTokenId() {
        return tokenId;
    }

    /**
     * 
     * @param tokenId
     *     The tokenId
     */
    @JsonProperty("tokenId")
    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public Authenticate withTokenId(String tokenId) {
        this.tokenId = tokenId;
        return this;
    }

    /**
     * 
     * @return
     *     The successUrl
     */
    @JsonProperty("successUrl")
    public String getSuccessUrl() {
        return successUrl;
    }

    /**
     * 
     * @param successUrl
     *     The successUrl
     */
    @JsonProperty("successUrl")
    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public Authenticate withSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
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

    public Authenticate withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(tokenId).append(successUrl).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Authenticate) == false) {
            return false;
        }
        Authenticate rhs = ((Authenticate) other);
        return new EqualsBuilder().append(tokenId, rhs.tokenId).append(successUrl, rhs.successUrl).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
