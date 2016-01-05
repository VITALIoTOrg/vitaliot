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
    "store",
    "retrieve"
})
public class PermissionsCollection {

    @JsonProperty("store")
    private Permissions store;
    @JsonProperty("retrieve")
    private Permissions retrieve ;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The store
     */
    @JsonProperty("store")
    public Permissions getStore() {
        return store;
    }

    /**
     * 
     * @param store
     *     The store
     */
    @JsonProperty("store")
    public void setStore(Permissions store) {
        this.store = store;
    }

    public PermissionsCollection withStore(Permissions store) {
        this.store = store;
        return this;
    }
    
    /**
     * 
     * @return
     *     The retrieve
     */
    @JsonProperty("retrieve")
    public Permissions getRetrieve() {
        return retrieve;
    }

    /**
     * 
     * @param retrieve
     *     The retrieve
     */
    @JsonProperty("retrieve")
    public void setRetrieve(Permissions retrieve) {
        this.retrieve = retrieve;
    }

    public PermissionsCollection withRetrieve(Permissions retrieve) {
        this.retrieve = retrieve;
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

    public PermissionsCollection withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
