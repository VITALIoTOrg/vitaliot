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

import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "attribute",
    "value"
})
public class AttributeValue {

    @JsonProperty("attribute")
    private String attribute;
    @JsonProperty("value")
    private String value ;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    
    /**
     * 
     * @return
     *     The attribute
     */
    @JsonProperty("attribute")
    public String getAttribute() {
        return attribute;
    }

    /**
     * 
     * @param attribute
     *     The attribute
     */
    @JsonProperty("attribute")
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public AttributeValue withAttribute(String attribute) {
        this.attribute = attribute;
        return this;
    }
    
    /**
     * 
     * @return
     *     The value
     */
    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    /**
     * 
     * @param value
     *     The value
     */
    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    public AttributeValue withValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
    @Override
    public boolean equals(Object other) {
    	String pattern, resource;
    	//boolean ha = false;
    	
        if (other == this) {
            return true;
        }
        if ((other instanceof AttributeValue) == false) {
            return false;
        }
        
        AttributeValue rhs = ((AttributeValue) other);
        
        if(value == null || attribute == null || rhs.getValue() == null || rhs.getAttribute() == null) // we cannot compare if anything is null
        	return false;
        // First thing we need to know which one was is a pattern and which is a real value
        if(value.contains("*")) {
        	pattern = value;
        	resource = rhs.getValue();
        } else {
        	pattern = rhs.getValue();
        	resource = value;
        }
        
        /*if(pattern.contains("*")) {
        	System.out.println("Pattern: " + pattern);
        	System.out.println("Resource: " + resource);
        	ha = true;
        }*/
    	
    	// Translate the OpenAM pattern into a Java regular expression
    	pattern = pattern.replaceAll("-\\*-", "[^/]*");
    	pattern = pattern.replaceAll("\\?\\*$", "\\?.+");
    	pattern = pattern.replaceAll("([^?]*)\\*(.*)", "$1.*$2");
    	pattern = pattern.replaceAll("\\?\\*.+", "\\?.*");
    	
    	/*if(ha)
    		System.out.println("Pattern Java: " + pattern);*/
    	
        return attribute.equals(rhs.getAttribute()) && resource.matches(pattern);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public AttributeValue withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
