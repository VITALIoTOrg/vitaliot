
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
    "realm",
    "uniqueMember",
    "cn",
    "dn",
    "objectclass",
    "universalid"
})
public class Group {

    @JsonProperty("username")
    private String username;
    @JsonProperty("realm")
    private String realm;
    @JsonProperty("uniqueMember")
    private List<String> uniqueMember = new ArrayList<String>();
    @JsonProperty("cn")
    private List<String> cn = new ArrayList<String>();
    @JsonProperty("dn")
    private List<String> dn = new ArrayList<String>();
    @JsonProperty("objectclass")
    private List<String> objectclass = new ArrayList<String>();
    @JsonProperty("universalid")
    private List<String> universalid = new ArrayList<String>();
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

    public Group withUsername(String username) {
        this.username = username;
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

    public Group withRealm(String realm) {
        this.realm = realm;
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

    public Group withUniqueMember(List<String> uniqueMember) {
        this.uniqueMember = uniqueMember;
        return this;
    }

    /**
     * 
     * @return
     *     The cn
     */
    @JsonProperty("cn")
    public List<String> getCn() {
        return cn;
    }

    /**
     * 
     * @param cn
     *     The cn
     */
    @JsonProperty("cn")
    public void setCn(List<String> cn) {
        this.cn = cn;
    }

    public Group withCn(List<String> cn) {
        this.cn = cn;
        return this;
    }

    /**
     * 
     * @return
     *     The dn
     */
    @JsonProperty("dn")
    public List<String> getDn() {
        return dn;
    }

    /**
     * 
     * @param dn
     *     The dn
     */
    @JsonProperty("dn")
    public void setDn(List<String> dn) {
        this.dn = dn;
    }

    public Group withDn(List<String> dn) {
        this.dn = dn;
        return this;
    }

    /**
     * 
     * @return
     *     The objectclass
     */
    @JsonProperty("objectclass")
    public List<String> getObjectclass() {
        return objectclass;
    }

    /**
     * 
     * @param objectclass
     *     The objectclass
     */
    @JsonProperty("objectclass")
    public void setObjectclass(List<String> objectclass) {
        this.objectclass = objectclass;
    }

    public Group withObjectclass(List<String> objectclass) {
        this.objectclass = objectclass;
        return this;
    }

    /**
     * 
     * @return
     *     The universalid
     */
    @JsonProperty("universalid")
    public List<String> getUniversalid() {
        return universalid;
    }

    /**
     * 
     * @param universalid
     *     The universalid
     */
    @JsonProperty("universalid")
    public void setUniversalid(List<String> universalid) {
        this.universalid = universalid;
    }

    public Group withUniversalid(List<String> universalid) {
        this.universalid = universalid;
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

    public Group withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(username).append(realm).append(uniqueMember).append(cn).append(dn).append(objectclass).append(universalid).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Group) == false) {
            return false;
        }
        Group rhs = ((Group) other);
        return new EqualsBuilder().append(username, rhs.username).append(realm, rhs.realm).append(uniqueMember, rhs.uniqueMember).append(cn, rhs.cn).append(dn, rhs.dn).append(objectclass, rhs.objectclass).append(universalid, rhs.universalid).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
