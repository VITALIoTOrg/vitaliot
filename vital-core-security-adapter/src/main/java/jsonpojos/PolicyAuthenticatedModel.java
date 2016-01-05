
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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "name",
    "active",
    "description",
    "resources",
    "actionValues",
    "subject"
})
public class PolicyAuthenticatedModel {

    @JsonProperty("name")
    private String name;
    @JsonProperty("active")
    private Boolean active;
    @JsonProperty("description")
    private String description;
    @JsonProperty("resources")
    private List<String> resources = new ArrayList<String>();
    @JsonProperty("actionValues")
    private ActionValues__ actionValues;
    @JsonProperty("subject")
    private Subject__ subject;
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

    public PolicyAuthenticatedModel withName(String name) {
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

    public PolicyAuthenticatedModel withActive(Boolean active) {
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

    public PolicyAuthenticatedModel withDescription(String description) {
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

    public PolicyAuthenticatedModel withResources(List<String> resources) {
        this.resources = resources;
        return this;
    }

    /**
     * 
     * @return
     *     The actionValues
     */
    @JsonProperty("actionValues")
    public ActionValues__ getActionValues() {
        return actionValues;
    }

    /**
     * 
     * @param actionValues
     *     The actionValues
     */
    @JsonProperty("actionValues")
    public void setActionValues(ActionValues__ actionValues) {
        this.actionValues = actionValues;
    }

    public PolicyAuthenticatedModel withActionValues(ActionValues__ actionValues) {
        this.actionValues = actionValues;
        return this;
    }

    /**
     * 
     * @return
     *     The subject
     */
    @JsonProperty("subject")
    public Subject__ getSubject() {
        return subject;
    }

    /**
     * 
     * @param subject
     *     The subject
     */
    @JsonProperty("subject")
    public void setSubject(Subject__ subject) {
        this.subject = subject;
    }

    public PolicyAuthenticatedModel withSubject(Subject__ subject) {
        this.subject = subject;
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

    public PolicyAuthenticatedModel withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(active).append(description).append(resources).append(actionValues).append(subject).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof PolicyAuthenticatedModel) == false) {
            return false;
        }
        PolicyAuthenticatedModel rhs = ((PolicyAuthenticatedModel) other);
        return new EqualsBuilder().append(name, rhs.name).append(active, rhs.active).append(description, rhs.description).append(resources, rhs.resources).append(actionValues, rhs.actionValues).append(subject, rhs.subject).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
