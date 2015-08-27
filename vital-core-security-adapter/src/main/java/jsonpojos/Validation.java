
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
    "valid",
    "uid",
    "realm",
    "name",
    "fullname",
    "mailhash",
    "creation"
})
public class Validation {

    @JsonProperty("valid")
    private Boolean valid;
    @JsonProperty("uid")
    private String uid;
    @JsonProperty("realm")
    private String realm;
    @JsonProperty("name")
    private String name;
    @JsonProperty("fullname")
    private String fullname;
    @JsonProperty("mailhash")
    private String mailhash;
    @JsonProperty("creation")
    private SimpleDate creation;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The valid
     */
    @JsonProperty("valid")
    public Boolean getValid() {
        return valid;
    }

    /**
     * 
     * @param valid
     *     The valid
     */
    @JsonProperty("valid")
    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Validation withValid(Boolean valid) {
        this.valid = valid;
        return this;
    }

    /**
     * 
     * @return
     *     The uid
     */
    @JsonProperty("uid")
    public String getUid() {
        return uid;
    }

    /**
     * 
     * @param uid
     *     The uid
     */
    @JsonProperty("uid")
    public void setUid(String uid) {
        this.uid = uid;
    }

    public Validation withUid(String uid) {
        this.uid = uid;
        return this;
    }

    /**
     * 
     * @return
     *     The realm
     */
    @JsonProperty("realm")
    public String getRealm() {
        return realm;
    }

    /**
     * 
     * @param realm
     *     The realm
     */
    @JsonProperty("realm")
    public void setRealm(String realm) {
        this.realm = realm;
    }

    public Validation withRealm(String realm) {
        this.realm = realm;
        return this;
    }

    /**
     * 
     * @return
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public Validation withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 
     * @return
     *     The fullname
     */
    @JsonProperty("fullname")
    public String getFullname() {
        return fullname;
    }

    /**
     * 
     * @param fullname
     *     The fullname
     */
    @JsonProperty("fullname")
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Validation withFullname(String fullname) {
        this.fullname = fullname;
        return this;
    }

    /**
     * 
     * @return
     *     The mailhash
     */
    @JsonProperty("mailhash")
    public String getMailhash() {
        return mailhash;
    }

    /**
     * 
     * @param mailhash
     *     The mailhash
     */
    @JsonProperty("mailhash")
    public void setMailhash(String mailhash) {
        this.mailhash = mailhash;
    }

    public Validation withMailhash(String mailhash) {
        this.mailhash = mailhash;
        return this;
    }

    /**
     * 
     * @return
     *     The creation
     */
    @JsonProperty("creation")
    public SimpleDate getCreation() {
        return creation;
    }

    /**
     * 
     * @param creation
     *     The creation
     */
    @JsonProperty("creation")
    public void setCreation(SimpleDate creation) {
        this.creation = creation;
    }

    public Validation withCreation(SimpleDate creation) {
        this.creation = creation;
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

    public Validation withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
