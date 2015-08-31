
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
    "uid",
    "mail",
    "userPassword",
    "sn",
    "createTimestamp",
    "cn",
    "modifyTimestamp",
    "givenName",
    "givenname",
    "inetUserStatus",
    "dn",
    "sun-fm-saml2-nameid-info",
    "objectClass",
    "universalid",
    "sun-fm-saml2-nameid-infokey"
})
public class User {

    @JsonProperty("username")
    private String username;
    @JsonProperty("realm")
    private String realm;
    @JsonProperty("uid")
    private List<String> uid = new ArrayList<String>();
    @JsonProperty("mail")
    private List<String> mail = new ArrayList<String>();
    @JsonProperty("userPassword")
    private List<String> userPassword = new ArrayList<String>();
    @JsonProperty("sn")
    private List<String> sn = new ArrayList<String>();
    @JsonProperty("createTimestamp")
    private List<String> createTimestamp = new ArrayList<String>();
    @JsonProperty("cn")
    private List<String> cn = new ArrayList<String>();
    @JsonProperty("modifyTimestamp")
    private List<String> modifyTimestamp = new ArrayList<String>();
    @JsonProperty("givenName")
    private List<String> givenName = new ArrayList<String>();
    @JsonProperty("givenname")
    private List<String> givenname = new ArrayList<String>();
    @JsonProperty("inetUserStatus")
    private List<String> inetUserStatus = new ArrayList<String>();
    @JsonProperty("dn")
    private List<String> dn = new ArrayList<String>();
    @JsonProperty("sun-fm-saml2-nameid-info")
    private List<String> sunFmSaml2NameidInfo = new ArrayList<String>();
    @JsonProperty("objectClass")
    private List<String> objectClass = new ArrayList<String>();
    @JsonProperty("universalid")
    private List<String> universalid = new ArrayList<String>();
    @JsonProperty("sun-fm-saml2-nameid-infokey")
    private List<String> sunFmSaml2NameidInfokey = new ArrayList<String>();
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

    public User withUsername(String username) {
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

    public User withRealm(String realm) {
        this.realm = realm;
        return this;
    }

    /**
     * 
     * @return
     *     The uid
     */
    @JsonProperty("uid")
    public List<String> getUid() {
        return uid;
    }

    /**
     * 
     * @param uid
     *     The uid
     */
    @JsonProperty("uid")
    public void setUid(List<String> uid) {
        this.uid = uid;
    }

    public User withUid(List<String> uid) {
        this.uid = uid;
        return this;
    }

    /**
     * 
     * @return
     *     The mail
     */
    @JsonProperty("mail")
    public List<String> getMail() {
        return mail;
    }

    /**
     * 
     * @param mail
     *     The mail
     */
    @JsonProperty("mail")
    public void setMail(List<String> mail) {
        this.mail = mail;
    }

    public User withMail(List<String> mail) {
        this.mail = mail;
        return this;
    }

    /**
     * 
     * @return
     *     The userPassword
     */
    @JsonProperty("userPassword")
    public List<String> getUserPassword() {
        return userPassword;
    }

    /**
     * 
     * @param userPassword
     *     The userPassword
     */
    @JsonProperty("userPassword")
    public void setUserPassword(List<String> userPassword) {
        this.userPassword = userPassword;
    }

    public User withUserPassword(List<String> userPassword) {
        this.userPassword = userPassword;
        return this;
    }

    /**
     * 
     * @return
     *     The sn
     */
    @JsonProperty("sn")
    public List<String> getSn() {
        return sn;
    }

    /**
     * 
     * @param sn
     *     The sn
     */
    @JsonProperty("sn")
    public void setSn(List<String> sn) {
        this.sn = sn;
    }

    public User withSn(List<String> sn) {
        this.sn = sn;
        return this;
    }

    /**
     * 
     * @return
     *     The createTimestamp
     */
    @JsonProperty("createTimestamp")
    public List<String> getCreateTimestamp() {
        return createTimestamp;
    }

    /**
     * 
     * @param createTimestamp
     *     The createTimestamp
     */
    @JsonProperty("createTimestamp")
    public void setCreateTimestamp(List<String> createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public User withCreateTimestamp(List<String> createTimestamp) {
        this.createTimestamp = createTimestamp;
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

    public User withCn(List<String> cn) {
        this.cn = cn;
        return this;
    }

    /**
     * 
     * @return
     *     The modifyTimestamp
     */
    @JsonProperty("modifyTimestamp")
    public List<String> getModifyTimestamp() {
        return modifyTimestamp;
    }

    /**
     * 
     * @param modifyTimestamp
     *     The modifyTimestamp
     */
    @JsonProperty("modifyTimestamp")
    public void setModifyTimestamp(List<String> modifyTimestamp) {
        this.modifyTimestamp = modifyTimestamp;
    }

    public User withModifyTimestamp(List<String> modifyTimestamp) {
        this.modifyTimestamp = modifyTimestamp;
        return this;
    }

    /**
     * 
     * @return
     *     The givenName
     */
    @JsonProperty("givenName")
    public List<String> getGivenName() {
        return givenName;
    }

    /**
     * 
     * @param givenName
     *     The givenName
     */
    @JsonProperty("givenName")
    public void setGivenName(List<String> givenName) {
        this.givenName = givenName;
    }

    public User withGivenName(List<String> givenName) {
        this.givenName = givenName;
        return this;
    }

    /**
     * 
     * @return
     *     The givenname
     */
    @JsonProperty("givenname")
    public List<String> getGivenname() {
        return givenname;
    }

    /**
     * 
     * @param givenname
     *     The givenname
     */
    @JsonProperty("givenname")
    public void setGivenname(List<String> givenname) {
        this.givenname = givenname;
    }

    public User withGivenname(List<String> givenname) {
        this.givenname = givenname;
        return this;
    }

    /**
     * 
     * @return
     *     The inetUserStatus
     */
    @JsonProperty("inetUserStatus")
    public List<String> getInetUserStatus() {
        return inetUserStatus;
    }

    /**
     * 
     * @param inetUserStatus
     *     The inetUserStatus
     */
    @JsonProperty("inetUserStatus")
    public void setInetUserStatus(List<String> inetUserStatus) {
        this.inetUserStatus = inetUserStatus;
    }

    public User withInetUserStatus(List<String> inetUserStatus) {
        this.inetUserStatus = inetUserStatus;
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

    public User withDn(List<String> dn) {
        this.dn = dn;
        return this;
    }

    /**
     * 
     * @return
     *     The sunFmSaml2NameidInfo
     */
    @JsonProperty("sun-fm-saml2-nameid-info")
    public List<String> getSunFmSaml2NameidInfo() {
        return sunFmSaml2NameidInfo;
    }

    /**
     * 
     * @param sunFmSaml2NameidInfo
     *     The sun-fm-saml2-nameid-info
     */
    @JsonProperty("sun-fm-saml2-nameid-info")
    public void setSunFmSaml2NameidInfo(List<String> sunFmSaml2NameidInfo) {
        this.sunFmSaml2NameidInfo = sunFmSaml2NameidInfo;
    }

    public User withSunFmSaml2NameidInfo(List<String> sunFmSaml2NameidInfo) {
        this.sunFmSaml2NameidInfo = sunFmSaml2NameidInfo;
        return this;
    }

    /**
     * 
     * @return
     *     The objectClass
     */
    @JsonProperty("objectClass")
    public List<String> getObjectClass() {
        return objectClass;
    }

    /**
     * 
     * @param objectClass
     *     The objectClass
     */
    @JsonProperty("objectClass")
    public void setObjectClass(List<String> objectClass) {
        this.objectClass = objectClass;
    }

    public User withObjectClass(List<String> objectClass) {
        this.objectClass = objectClass;
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

    public User withUniversalid(List<String> universalid) {
        this.universalid = universalid;
        return this;
    }

    /**
     * 
     * @return
     *     The sunFmSaml2NameidInfokey
     */
    @JsonProperty("sun-fm-saml2-nameid-infokey")
    public List<String> getSunFmSaml2NameidInfokey() {
        return sunFmSaml2NameidInfokey;
    }

    /**
     * 
     * @param sunFmSaml2NameidInfokey
     *     The sun-fm-saml2-nameid-infokey
     */
    @JsonProperty("sun-fm-saml2-nameid-infokey")
    public void setSunFmSaml2NameidInfokey(List<String> sunFmSaml2NameidInfokey) {
        this.sunFmSaml2NameidInfokey = sunFmSaml2NameidInfokey;
    }

    public User withSunFmSaml2NameidInfokey(List<String> sunFmSaml2NameidInfokey) {
        this.sunFmSaml2NameidInfokey = sunFmSaml2NameidInfokey;
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

    public User withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(username).append(realm).append(uid).append(mail).append(userPassword).append(sn).append(createTimestamp).append(cn).append(modifyTimestamp).append(givenName).append(givenname).append(inetUserStatus).append(dn).append(sunFmSaml2NameidInfo).append(objectClass).append(universalid).append(sunFmSaml2NameidInfokey).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof User) == false) {
            return false;
        }
        User rhs = ((User) other);
        return new EqualsBuilder().append(username, rhs.username).append(realm, rhs.realm).append(uid, rhs.uid).append(mail, rhs.mail).append(userPassword, rhs.userPassword).append(sn, rhs.sn).append(createTimestamp, rhs.createTimestamp).append(cn, rhs.cn).append(modifyTimestamp, rhs.modifyTimestamp).append(givenName, rhs.givenName).append(givenname, rhs.givenname).append(inetUserStatus, rhs.inetUserStatus).append(dn, rhs.dn).append(sunFmSaml2NameidInfo, rhs.sunFmSaml2NameidInfo).append(objectClass, rhs.objectClass).append(universalid, rhs.universalid).append(sunFmSaml2NameidInfokey, rhs.sunFmSaml2NameidInfokey).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
