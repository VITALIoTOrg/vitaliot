
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
    "POST",
    "PATCH",
    "GET",
    "DELETE",
    "OPTIONS",
    "PUT",
    "HEAD",
    "RETRIEVE",
    "STORE"
})
public class ActionValues {

    @JsonProperty("POST")
    private Boolean POST;
    @JsonProperty("PATCH")
    private Boolean PATCH;
    @JsonProperty("GET")
    private Boolean GET;
    @JsonProperty("DELETE")
    private Boolean DELETE;
    @JsonProperty("OPTIONS")
    private Boolean OPTIONS;
    @JsonProperty("PUT")
    private Boolean PUT;
    @JsonProperty("HEAD")
    private Boolean HEAD;
    @JsonProperty("RETRIEVE")
    private Boolean RETRIEVE;
    @JsonProperty("STORE")
    private Boolean STORE;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The POST
     */
    @JsonProperty("POST")
    public Boolean getPOST() {
        return POST;
    }

    /**
     * 
     * @param POST
     *     The POST
     */
    @JsonProperty("POST")
    public void setPOST(Boolean POST) {
        this.POST = POST;
    }

    public ActionValues withPOST(Boolean POST) {
        this.POST = POST;
        return this;
    }

    /**
     * 
     * @return
     *     The PATCH
     */
    @JsonProperty("PATCH")
    public Boolean getPATCH() {
        return PATCH;
    }

    /**
     * 
     * @param PATCH
     *     The PATCH
     */
    @JsonProperty("PATCH")
    public void setPATCH(Boolean PATCH) {
        this.PATCH = PATCH;
    }

    public ActionValues withPATCH(Boolean PATCH) {
        this.PATCH = PATCH;
        return this;
    }

    /**
     * 
     * @return
     *     The GET
     */
    @JsonProperty("GET")
    public Boolean getGET() {
        return GET;
    }

    /**
     * 
     * @param GET
     *     The GET
     */
    @JsonProperty("GET")
    public void setGET(Boolean GET) {
        this.GET = GET;
    }

    public ActionValues withGET(Boolean GET) {
        this.GET = GET;
        return this;
    }

    /**
     * 
     * @return
     *     The DELETE
     */
    @JsonProperty("DELETE")
    public Boolean getDELETE() {
        return DELETE;
    }

    /**
     * 
     * @param DELETE
     *     The DELETE
     */
    @JsonProperty("DELETE")
    public void setDELETE(Boolean DELETE) {
        this.DELETE = DELETE;
    }

    public ActionValues withDELETE(Boolean DELETE) {
        this.DELETE = DELETE;
        return this;
    }

    /**
     * 
     * @return
     *     The OPTIONS
     */
    @JsonProperty("OPTIONS")
    public Boolean getOPTIONS() {
        return OPTIONS;
    }

    /**
     * 
     * @param OPTIONS
     *     The OPTIONS
     */
    @JsonProperty("OPTIONS")
    public void setOPTIONS(Boolean OPTIONS) {
        this.OPTIONS = OPTIONS;
    }

    public ActionValues withOPTIONS(Boolean OPTIONS) {
        this.OPTIONS = OPTIONS;
        return this;
    }

    /**
     * 
     * @return
     *     The PUT
     */
    @JsonProperty("PUT")
    public Boolean getPUT() {
        return PUT;
    }

    /**
     * 
     * @param PUT
     *     The PUT
     */
    @JsonProperty("PUT")
    public void setPUT(Boolean PUT) {
        this.PUT = PUT;
    }

    public ActionValues withPUT(Boolean PUT) {
        this.PUT = PUT;
        return this;
    }

    /**
     * 
     * @return
     *     The HEAD
     */
    @JsonProperty("HEAD")
    public Boolean getHEAD() {
        return HEAD;
    }

    /**
     * 
     * @param HEAD
     *     The HEAD
     */
    @JsonProperty("HEAD")
    public void setHEAD(Boolean HEAD) {
        this.HEAD = HEAD;
    }

    public ActionValues withHEAD(Boolean HEAD) {
        this.HEAD = HEAD;
        return this;
    }
    
    /**
     * 
     * @return
     *     The RETRIEVE
     */
    @JsonProperty("RETRIEVE")
    public Boolean getRETRIEVE() {
        return RETRIEVE;
    }

    /**
     * 
     * @param RETRIEVE
     *     The RETRIEVE
     */
    @JsonProperty("RETRIEVE")
    public void setRETRIEVE(Boolean RETRIEVE) {
        this.RETRIEVE = RETRIEVE;
    }

    public ActionValues withRETRIEVE(Boolean RETRIEVE) {
        this.RETRIEVE = RETRIEVE;
        return this;
    }

    /**
     * 
     * @return
     *     The STORE
     */
    @JsonProperty("STORE")
    public Boolean getSTORE() {
        return STORE;
    }

    /**
     * 
     * @param STORE
     *     The STORE
     */
    @JsonProperty("STORE")
    public void setSTORE(Boolean STORE) {
        this.STORE = STORE;
    }

    public ActionValues withSTORE(Boolean STORE) {
        this.STORE = STORE;
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

    public ActionValues withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(POST).append(PATCH).append(GET).append(DELETE).append(OPTIONS).append(PUT).append(HEAD).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ActionValues) == false) {
            return false;
        }
        ActionValues rhs = ((ActionValues) other);
        return new EqualsBuilder().append(POST, rhs.POST).append(PATCH, rhs.PATCH).append(GET, rhs.GET).append(DELETE, rhs.DELETE).append(OPTIONS, rhs.OPTIONS).append(PUT, rhs.PUT).append(HEAD, rhs.HEAD).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
