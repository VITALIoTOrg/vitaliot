
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
    "description",
    "resources"
})
public class Application {

    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("resources")
    private List<String> resources = new ArrayList<String>();
    @JsonProperty("subjects")
    private List<String> subjects = new ArrayList<String>();
    @JsonProperty("conditions")
    private List<String> conditions = new ArrayList<String>();
    @JsonProperty("actions")
    private ActionValues___ actions;
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

    public Application withName(String name) {
        this.name = name;
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

    public Application withDescription(String description) {
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

    public Application withResources(List<String> resources) {
        this.resources = resources;
        return this;
    }
    
    /**
     * 
     * @return
     *     The subjects
     */
    @JsonProperty("subjects")
    public List<String> getSubjects() {
        return subjects;
    }

    /**
     * 
     * @param subjects
     *     The subjects
     */
    @JsonProperty("subjects")
    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public Application withSubjects(List<String> subjects) {
        this.subjects = subjects;
        return this;
    }
    
    /**
     * 
     * @return
     *     The conditions
     */
    @JsonProperty("conditions")
    public List<String> getConditions() {
        return conditions;
    }

    /**
     * 
     * @param conditions
     *     The conditions
     */
    @JsonProperty("conditions")
    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }

    public Application withConditions(List<String> conditions) {
        this.conditions = conditions;
        return this;
    }
    
    /**
     * 
     * @return
     *     The actions
     */
    @JsonProperty("actions")
    public ActionValues___ getActions() {
        return actions;
    }

    /**
     * 
     * @param actions
     *     The actions
     */
    @JsonProperty("actions")
    public void setActions(ActionValues___ actions) {
        this.actions = actions;
    }

    public Application withActions(ActionValues___ actions) {
        this.actions = actions;
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

    public Application withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
