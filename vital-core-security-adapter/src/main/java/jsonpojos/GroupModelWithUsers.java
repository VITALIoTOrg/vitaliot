
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
    "username",
    "uniqueMember"
})
public class GroupModelWithUsers {

    @JsonProperty("username")
    private String username;
    @JsonProperty("uniqueMember")
    private List<String> uniqueMember = new ArrayList<String>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The username
     */
    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    /**
     * 
     * @param username
     *     The username
     */
    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    public GroupModelWithUsers withUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * 
     * @return
     *     The uniqueMember
     */
    @JsonProperty("uniqueMember")
    public List<String> getUniqueMember() {
        return uniqueMember;
    }

    /**
     * 
     * @param uniqueMember
     *     The uniqueMember
     */
    @JsonProperty("uniqueMember")
    public void setUniqueMember(List<String> uniqueMember) {
        this.uniqueMember = uniqueMember;
    }

    public GroupModelWithUsers withUniqueMember(List<String> uniqueMember) {
        this.uniqueMember = uniqueMember;
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

    public GroupModelWithUsers withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(username).append(uniqueMember).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GroupModelWithUsers) == false) {
            return false;
        }
        GroupModelWithUsers rhs = ((GroupModelWithUsers) other);
        return new EqualsBuilder().append(username, rhs.username).append(uniqueMember, rhs.uniqueMember).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
