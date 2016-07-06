package jsonpojos;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
})
public class PPIResponse {

    @JsonIgnore
    private Map<String, Object> properties = new HashMap<String, Object>();

    public PPIResponse() {
    }
    
    public PPIResponse(PPIResponse resp) {
        if(resp != null)
            properties.putAll(resp.properties);
    }
    
    @SuppressWarnings("unchecked")
    public PPIResponse(Object map) {
        if(map != null && map instanceof LinkedHashMap<?, ?>)
            properties.putAll((LinkedHashMap<String, Object>) map);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    @JsonAnySetter
    public void setProperty(String name, Object value) {
        this.properties.put(name, value);
    }

    public PPIResponse withProperty(String name, Object value) {
        this.properties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(properties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof PPIResponse) == false) {
            return false;
        }
        PPIResponse rhs = ((PPIResponse) other);
        return new EqualsBuilder().append(properties, rhs.properties).isEquals();
    }

}
