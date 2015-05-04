
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
    "username",
    "userpassword",
    "mail"
})
public class UserModel {

    @JsonProperty("username")
    private String username;
    @JsonProperty("userpassword")
    private String userpassword;
    @JsonProperty("mail")
    private String mail;
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

    public UserModel withUsername(String username) {
        this.username = username;
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

    public UserModel withUserpassword(String userpassword) {
        this.userpassword = userpassword;
        return this;
    }

    /**
     * 
     * @return
     *     The mail
     */
    @JsonProperty("mail")
    public String getMail() {
        return mail;
    }

    /**
     * 
     * @param mail
     *     The mail
     */
    @JsonProperty("mail")
    public void setMail(String mail) {
        this.mail = mail;
    }

    public UserModel withMail(String mail) {
        this.mail = mail;
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

    public UserModel withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
