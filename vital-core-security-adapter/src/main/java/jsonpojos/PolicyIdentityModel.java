
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
    "name",
    "active",
    "description",
    "resources",
    "applicationName",
    "actionValues",
    "subject"
})
public class PolicyIdentityModel {

    @JsonProperty("name")
    private String name;
    @JsonProperty("active")
    private Boolean active;
    @JsonProperty("description")
    private String description;
    @JsonProperty("resources")
    private List<String> resources = new ArrayList<String>();
    @JsonProperty("applicationName")
    private String applicationName;
    @JsonProperty("actionValues")
    private ActionValues___ actionValues;
    @JsonProperty("subject")
    private Subject___ subject;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    public PolicyIdentityModel withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 
     * @return
     *     The active
     */
    @JsonProperty("active")
    public Boolean getActive() {
        return active;
    }

    /**
     * 
     * @param active
     *     The active
     */
    @JsonProperty("active")
    public void setActive(Boolean active) {
        this.active = active;
    }

    public PolicyIdentityModel withActive(Boolean active) {
        this.active = active;
        return this;
    }

    /**
     * 
     * @return
     *     The description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *     The description
     */
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    public PolicyIdentityModel withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * 
     * @return
     *     The resources
     */
    @JsonProperty("resources")
    public List<String> getResources() {
        return resources;
    }

    /**
     * 
     * @param resources
     *     The resources
     */
    @JsonProperty("resources")
    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    public PolicyIdentityModel withResources(List<String> resources) {
        this.resources = resources;
        return this;
    }

    /**
     * 
     * @return
     *     The applicationName
     */
    @JsonProperty("applicationName")
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * 
     * @param applicationName
     *     The applicationName
     */
    @JsonProperty("applicationName")
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public PolicyIdentityModel withApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    /**
     * 
     * @return
     *     The actionValues
     */
    @JsonProperty("actionValues")
    public ActionValues___ getActionValues() {
        return actionValues;
    }

    /**
     * 
     * @param actionValues
     *     The actionValues
     */
    @JsonProperty("actionValues")
    public void setActionValues(ActionValues___ actionValues) {
        this.actionValues = actionValues;
    }

    public PolicyIdentityModel withActionValues(ActionValues___ actionValues) {
        this.actionValues = actionValues;
        return this;
    }

    /**
     * 
     * @return
     *     The subject
     */
    @JsonProperty("subject")
    public Subject___ getSubject() {
        return subject;
    }

    /**
     * 
     * @param subject
     *     The subject
     */
    @JsonProperty("subject")
    public void setSubject(Subject___ subject) {
        this.subject = subject;
    }

    public PolicyIdentityModel withSubject(Subject___ subject) {
        this.subject = subject;
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

    public PolicyIdentityModel withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
