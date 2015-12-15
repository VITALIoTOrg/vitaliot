
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
    "type"
})
public class Subject {

    @JsonProperty("type")
    private String type;
    @JsonProperty("subjectValues")
    private List<String> subjectValues = new ArrayList<String>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The type
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    public Subject withType(String type) {
        this.type = type;
        return this;
    }
    
    /**
     * 
     * @return
     *     The subjectValues
     */
    @JsonProperty("subjectValues")
    public List<String> getSubjectValues() {
        return subjectValues;
    }

    /**
     * 
     * @param subjectValues
     *     The subjectValues
     */
    @JsonProperty("subjectValues")
    public void setSubjectValues(List<String> subjectValues) {
        this.subjectValues = subjectValues;
    }

    public Subject withSubjectValues(List<String> subjectValues) {
        this.subjectValues = subjectValues;
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

    public Subject withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(type).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Subject) == false) {
            return false;
        }
        Subject rhs = ((Subject) other);
        return new EqualsBuilder().append(type, rhs.type).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
