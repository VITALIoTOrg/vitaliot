package securitywrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;

import jsonpojos.Application;
import jsonpojos.Authenticate;
import jsonpojos.AuthenticationResponse;
import jsonpojos.DecisionArray;
import jsonpojos.GenericObject;
import jsonpojos.Group;
import jsonpojos.LogoutResponse;
import jsonpojos.Policy;
import jsonpojos.SimpleDate;
import jsonpojos.User;
import utils.Action;
import utils.JsonUtils;
import utils.MD5Util;
import utils.UTF8fixer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import clients.OpenAMClient;

@Path("/rest")
public class PostServices {
    private OpenAMClient client;
    
    public PostServices() {
        client = new OpenAMClient();
    }
    
    @Path("/user/create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(
            @FormParam("givenName") String givenName,
            @FormParam("surname") String surname,
            @FormParam("name") String username,
            @FormParam("password") String password,
            @FormParam("mail") String mail,
            @HeaderParam("Cookie") String cookie) {
        int code;
        StringBuilder answer = new StringBuilder();
        User user = new User();
        
        String token = null;
        if (cookie != null)
            token = cookie.replaceAll(".*ssoToken=([^;]*).*", "$1");
        
        code = 0;
        
        if (client.createUser(UTF8fixer.convert(givenName), UTF8fixer.convert(surname), UTF8fixer.convert(username), UTF8fixer.convert(password), UTF8fixer.convert(mail), answer, token)) {
            try {
                user = (User) JsonUtils.deserializeJson(answer.toString(), User.class);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            if (user.getAdditionalProperties().containsKey("code")) {
                if (user.getAdditionalProperties().get("code").getClass() == Integer.class) {
                    code = (Integer) user.getAdditionalProperties().get("code");
                }
            }
            if (code >= 400 && code < 500) {
                return Response.status(Status.BAD_REQUEST)
                    .entity(answer.toString())
                    .build();
            }
            else if (code >= 500 && code < 600) {
                return Response.status(Status.INTERNAL_SERVER_ERROR)
                        .entity(answer.toString())
                        .build();
            }
            else {
                return Response.ok()
                    .entity(answer.toString())
                    .build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(answer.toString())
                .build();
        }
    }
    
    @Path("/user/delete")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(
            @FormParam("name") String username,
            @HeaderParam("Cookie") String cookie) {
        Boolean result;
        int code;
        StringBuilder answer = new StringBuilder();
        User user = new User();
        
        String token = null;
        if (cookie != null)
            token = cookie.replaceAll(".*ssoToken=([^;]*).*", "$1");
        
        code = 0;
        
        result = client.deleteUser(UTF8fixer.convert(username), answer, token);
        
        try {
            user = (User) JsonUtils.deserializeJson(answer.toString(), User.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (user.getAdditionalProperties().containsKey("code")) {
            if (user.getAdditionalProperties().get("code").getClass() == Integer.class) {
                code = (Integer) user.getAdditionalProperties().get("code");
            }
        }
        
        if (result) {
            return Response.ok()
                .entity(answer.toString())
                .build();
        } else {
            
            if (code >= 400 && code < 500) {
                return Response.status(Status.BAD_REQUEST)
                    .entity(answer.toString())
                    .build();
            }
            else {
                return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(answer.toString())
                    .build();
            }
        }
    }
    
    @Path("/group/create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGroup(
            @FormParam("name") String name,
            @HeaderParam("Cookie") String cookie) {
        int code;
        StringBuilder answer = new StringBuilder();
        Group group = new Group();
        
        String token = null;
        if (cookie != null)
            token = cookie.replaceAll(".*ssoToken=([^;]*).*", "$1");
        
        code = 0;
       
        if (client.createGroup(UTF8fixer.convert(name), answer, token)) {
            try {
                group = (Group) JsonUtils.deserializeJson(answer.toString(), Group.class);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            if (group.getAdditionalProperties().containsKey("code")) {
                if (group.getAdditionalProperties().get("code").getClass() == Integer.class) {
                    code = (Integer) group.getAdditionalProperties().get("code");
                }
            }
            if (code >= 400 && code < 500) {
                return Response.status(Status.BAD_REQUEST)
                    .entity(answer.toString())
                    .build();
            }
            else if (code >= 500 && code < 600) {
                return Response.status(Status.INTERNAL_SERVER_ERROR)
                        .entity(answer.toString())
                        .build();
            }
            else {
                return Response.ok()
                    .entity(answer.toString())
                    .build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(answer.toString())
                .build();
        }
    }
    
    @Path("/group/delete")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteGroup(
            @FormParam("name") String name,
            @HeaderParam("Cookie") String cookie) {
        Boolean result;
        int code;
        StringBuilder answer = new StringBuilder();
        Group group = new Group();
        
        String token = null;
        if (cookie != null)
            token = cookie.replaceAll(".*ssoToken=([^;]*).*", "$1");
        
        code = 0;
        
        result = client.deleteGroup(UTF8fixer.convert(name), answer, token);
        
        try {
            group = (Group) JsonUtils.deserializeJson(answer.toString(), Group.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (group.getAdditionalProperties().containsKey("code")) {
            if (group.getAdditionalProperties().get("code").getClass() == Integer.class) {
                code = (Integer) group.getAdditionalProperties().get("code");
            }
        }
        
        if (result) {

            return Response.ok()
                .entity(answer.toString())
                .build();
        } else {
            
            if (code >= 400 && code < 500) {
                return Response.status(Status.BAD_REQUEST)
                    .entity(answer.toString())
                    .build();
            }
            else {
                return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(answer.toString())
                    .build();
            }
        }
    }
    
    @Path("/group/{id}/addUser")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUserToGroup(
            @PathParam("id") String groupId,
            @FormParam("user") String username,
            @HeaderParam("Cookie") String cookie) {
        int code;
        StringBuilder answer = new StringBuilder();
        Group group = new Group();
        
        ArrayList<String> usersList = new ArrayList<String>();
        usersList.add(UTF8fixer.convert(username));
        
        String token = null;
        if (cookie != null)
            token = cookie.replaceAll(".*ssoToken=([^;]*).*", "$1");
        
        code = 0;
        
        if (client.addUsersToGroup(UTF8fixer.convert(groupId), usersList, answer, token)) {
            try {
                group = (Group) JsonUtils.deserializeJson(answer.toString(), Group.class);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            if (group.getAdditionalProperties().containsKey("code")) {
                if (group.getAdditionalProperties().get("code").getClass() == Integer.class) {
                    code = (Integer) group.getAdditionalProperties().get("code");
                }
            }
            if (code >= 400 && code < 500) {
                return Response.status(Status.BAD_REQUEST)
                    .entity(answer.toString())
                    .build();
            }
            else if (code >= 500 && code < 600) {
                return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(answer.toString())
                    .build();
            }
            else {
                return Response.ok()
                    .entity(answer.toString())
                    .build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(answer.toString())
                .build();
        }
    }
    
    @Path("/group/{id}/delUser")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeUserFromGroup(
            @PathParam("id") String groupId,
            @FormParam("user") String username,
            @HeaderParam("Cookie") String cookie) {
        int code;
        StringBuilder answer = new StringBuilder();
        Group group = new Group();
        
        ArrayList<String> usersList = new ArrayList<String>();
        usersList.add(UTF8fixer.convert(username));
        
        String token = null;
        if (cookie != null)
            token = cookie.replaceAll(".*ssoToken=([^;]*).*", "$1");
        
        code = 0;
        
        if (client.deleteUsersFromGroup(UTF8fixer.convert(groupId), usersList, answer, token)) {
            
            try {
                group = (Group) JsonUtils.deserializeJson(answer.toString(), Group.class);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            if (group.getAdditionalProperties().containsKey("code")) {
                if (group.getAdditionalProperties().get("code").getClass() == Integer.class) {
                    code = (Integer) group.getAdditionalProperties().get("code");
                }
            }
            if (code >= 400 && code < 500) {
                return Response.status(Status.BAD_REQUEST)
                    .entity(answer.toString())
                    .build();
            }
            else if (code >= 500 && code < 600) {
                return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(answer.toString())
                    .build();
            }
            else {
                return Response.ok()
                    .entity(answer.toString())
                    .build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(answer.toString())
                .build();
        }
    }
    
    @Path("/policy/create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPolicy(
            @FormParam("name") String name,
            @FormParam("description") String description,
            @FormParam("appname") String appname,
            @FormParam("resources[]") ArrayList<String> res,
            @FormParam("groups[]") ArrayList<String> grs,
            @FormParam("actions[DELETE]") Boolean delete,
            @FormParam("actions[GET]") Boolean get,
            @FormParam("actions[HEAD]") Boolean head,
            @FormParam("actions[OPTIONS]") Boolean options,
            @FormParam("actions[PATCH]") Boolean patch,
            @FormParam("actions[POST]") Boolean post,
            @FormParam("actions[PUT]") Boolean put,
            @FormParam("actions[RETRIEVE]") Boolean retrieve,
            @FormParam("actions[STORE]") Boolean store,
            @HeaderParam("Cookie") String cookie) {
        int code;
        StringBuilder answer = new StringBuilder();
        Policy policy = new Policy();
        ArrayList<Action> actions = new ArrayList<Action>();
        Boolean result;
        
        if (delete != null) {
            actions.add(new Action("DELETE", delete.booleanValue()));
        }
        if (get != null) {
            actions.add(new Action("GET", get.booleanValue()));
        }
        if (head != null) {
            actions.add(new Action("HEAD", head.booleanValue()));
        }
        if (options != null) {
            actions.add(new Action("OPTIONS", options.booleanValue()));
        }
        if (patch != null) {
            actions.add(new Action("PATCH", patch.booleanValue()));
        }
        if (post != null) {
            actions.add(new Action("POST", post.booleanValue()));
        }
        if (put != null) {
            actions.add(new Action("PUT", put.booleanValue()));
        }
        if (retrieve != null) {
            actions.add(new Action("RETRIEVE", retrieve.booleanValue()));
        }
        if (store != null) {
            actions.add(new Action("STORE", store.booleanValue()));
        }
        
        code = 0;
        
        String token = null;
        if (cookie != null)
            token = cookie.replaceAll(".*ssoToken=([^;]*).*", "$1");

        ArrayList<String> resCor = new ArrayList<String>();
        for (int i = 0; i < res.size(); i++) {
            resCor.add(UTF8fixer.convert(res.get(i)));
        }

        ArrayList<String> grsCor = new ArrayList<String>();
        for (int i = 0; i < grs.size(); i++) {
            grsCor.add(UTF8fixer.convert(grs.get(i)));
        }
        
        result = client.createIdentityGroupsPolicy(UTF8fixer.convert(name), UTF8fixer.convert(description), actions, resCor, grsCor, UTF8fixer.convert(appname), answer, token);
        
        try {
            policy = (Policy) JsonUtils.deserializeJson(answer.toString(), Policy.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (result) {
            return Response.ok()
                .entity(answer.toString())
                .build();
        } else {
            if (policy.getAdditionalProperties().containsKey("code")) {
                if (policy.getAdditionalProperties().get("code").getClass() == Integer.class) {
                    code = (Integer) policy.getAdditionalProperties().get("code");
                }
            }
            if (code >= 400 && code < 500) {
                return Response.status(Status.BAD_REQUEST)
                    .entity(answer.toString())
                    .build();
            }
            else {
                return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(answer.toString())
                    .build();
            }
        }
    }
 
    @Path("/policy/delete")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePolicy(
            @FormParam("name") String name,
            @HeaderParam("Cookie") String cookie) {
        Boolean result;
        int code;
        StringBuilder answer = new StringBuilder();
        Policy policy = new Policy();
        
        String token = null;
        if (cookie != null)
            token = cookie.replaceAll(".*ssoToken=([^;]*).*", "$1");
        
        code = 0;
        
        result = client.deletePolicy(UTF8fixer.convert(name), answer, token);
        
        try {
            policy = (Policy) JsonUtils.deserializeJson(answer.toString(), Policy.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (policy.getAdditionalProperties().containsKey("code")) {
            if (policy.getAdditionalProperties().get("code").getClass() == Integer.class) {
                code = (Integer) policy.getAdditionalProperties().get("code");
            }
        }
        
        if (result) {
            return Response.ok()
                .entity(answer.toString())
                .build();
        } else {
            if (code >= 400 && code < 500) {
                return Response.status(Status.BAD_REQUEST)
                    .entity(answer.toString())
                    .build();
            }
            else {
                return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(answer.toString())
                    .build();
            }
        }
    }
    
    @Path("/application/create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createApplication(
            @FormParam("name") String name,
            @FormParam("type") String type,
            @FormParam("description") String description,
            @FormParam("resources[]") ArrayList<String> res,
            @FormParam("actions[DELETE]") Boolean delete,
            @FormParam("actions[GET]") Boolean get,
            @FormParam("actions[HEAD]") Boolean head,
            @FormParam("actions[OPTIONS]") Boolean options,
            @FormParam("actions[PATCH]") Boolean patch,
            @FormParam("actions[POST]") Boolean post,
            @FormParam("actions[PUT]") Boolean put,
            @FormParam("actions[RETRIEVE]") Boolean retrieve,
            @FormParam("actions[STORE]") Boolean store,
            @HeaderParam("Cookie") String cookie) {
        int code;
        StringBuilder answer = new StringBuilder();
        Application application = new Application();
        Boolean result;
        
        String token = null;
        if (cookie != null)
            token = cookie.replaceAll(".*ssoToken=([^;]*).*", "$1");
        
        code = 0;
        
        ArrayList<Action> actions = new ArrayList<Action>();
        
        if (delete != null) {
            actions.add(new Action("DELETE", delete.booleanValue()));
        }
        if (get != null) {
            actions.add(new Action("GET", get.booleanValue()));
        }
        if (head != null) {
            actions.add(new Action("HEAD", head.booleanValue()));
        }
        if (options != null) {
            actions.add(new Action("OPTIONS", options.booleanValue()));
        }
        if (patch != null) {
            actions.add(new Action("PATCH", patch.booleanValue()));
        }
        if (post != null) {
            actions.add(new Action("POST", post.booleanValue()));
        }
        if (put != null) {
            actions.add(new Action("PUT", put.booleanValue()));
        }
        if (retrieve != null) {
            actions.add(new Action("RETRIEVE", retrieve.booleanValue()));
        }
        if (store != null) {
            actions.add(new Action("STORE", store.booleanValue()));
        }

        ArrayList<String> resCor = new ArrayList<String>();
        for (int i = 0; i < res.size(); i++) {
            resCor.add(UTF8fixer.convert(res.get(i)));
        }
        
        result = client.createApplication(UTF8fixer.convert(type), UTF8fixer.convert(name), UTF8fixer.convert(description), resCor, actions, answer, token);
        
        try {
            application = (Application) JsonUtils.deserializeJson(answer.toString(), Application.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (result) {
            return Response.ok()
                .entity(answer.toString())
                .build();
        } else {
            if (application.getAdditionalProperties().containsKey("code")) {
                if (application.getAdditionalProperties().get("code").getClass() == Integer.class) {
                    code = (Integer) application.getAdditionalProperties().get("code");
                }
            }
            if (code >= 400 && code < 500) {
                return Response.status(Status.BAD_REQUEST)
                    .entity(answer.toString())
                    .build();
            }
            else {
                return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(answer.toString())
                    .build();
            }
        }
    }
    
    @Path("/application/delete")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteApplication(
            @FormParam("name") String name,
            @HeaderParam("Cookie") String cookie) {
        Boolean result;
        int code;
        StringBuilder answer = new StringBuilder();
        // Change policy with application once we have the class
        Application application = new Application();
        
        String token = null;
        if (cookie != null)
            token = cookie.replaceAll(".*ssoToken=([^;]*).*", "$1");
        
        code = 0;
        
        result = client.deleteApplication(UTF8fixer.convert(name), answer, token);
        
        try {
            application = (Application) JsonUtils.deserializeJson(answer.toString(), Application.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (application.getAdditionalProperties().containsKey("code")) {
            if (application.getAdditionalProperties().get("code").getClass() == Integer.class) {
                code = (Integer) application.getAdditionalProperties().get("code");
            }
        }
        
        if (result) {
            return Response.ok()
                .entity(answer.toString())
                .build();
        } else {
            if (code >= 400 && code < 500) {
                return Response.status(Status.BAD_REQUEST)
                    .entity(answer.toString())
                    .build();
            }
            else {
                return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(answer.toString())
                    .build();
            }
        }
    }
    
    @Path("/user/{id}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(
            @PathParam("id") String userId,
            @FormParam("givenName") String givenName,
            @FormParam("surname") String surname,
            @FormParam("mail") String mail,
            @FormParam("status") String status,
            @HeaderParam("Cookie") String cookie) {
        int code;
        StringBuilder answer = new StringBuilder();
        String userJson = null;
        User user = new User();

        //System.out.println(userId);
        
        String token = null;
        if (cookie != null)
            token = cookie.replaceAll(".*ssoToken=([^;]*).*", "$1");
        
        code = 0;
        
        if (client.updateUser(UTF8fixer.convert(userId), UTF8fixer.convert(givenName), UTF8fixer.convert(surname), UTF8fixer.convert(mail), status, answer, token)) {
            try {
                user = (User) JsonUtils.deserializeJson(answer.toString(), User.class);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            if (user.getAdditionalProperties().containsKey("code")) {
                if (user.getAdditionalProperties().get("code").getClass() == Integer.class) {
                    code = (Integer) user.getAdditionalProperties().get("code");
                }
            }
            if (code >= 400 && code < 500) {
                return Response.status(Status.BAD_REQUEST)
                    .entity(answer.toString())
                    .build();
            }
            else if (code >= 500 && code < 600) {
                return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(answer.toString())
                    .build();
            }
            else {
                List<String> gn = null;
                gn = user.getGivenName();
                if ((gn != null) && (!gn.isEmpty())) { // send back the first name if available
                    if (gn.get(0).equals(" "))
                        user.setGivenName(null);
                }
                
                gn = user.getGivenname();
                if ((gn != null) && (!gn.isEmpty())) {
                    if (gn.get(0).equals(" "))
                        user.setGivenName(null);
                }
                try {
                    userJson = JsonUtils.serializeJson(user);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return Response.ok()
                    .entity(userJson)
                    .build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(answer.toString())
                .build();
        }
    }
    
    @Path("/user/changePassword")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePassword(
            @HeaderParam("Cookie") String cookie,
            @FormParam("userpass") String userPass,
            @FormParam("currpass") String currPass) {
        GenericObject resp;
        String answer;
        int code;
        
        String token = null;
        if (cookie != null)
            token = cookie.replaceAll(".*ssoToken=([^;]*).*", "$1");
        
        answer = null;
        code = 0;
        resp = client.changePassword(token, UTF8fixer.convert(userPass), UTF8fixer.convert(currPass));
        
        try {
            answer = JsonUtils.serializeJson(resp);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (resp.getAdditionalProperties().containsKey("code")) {
            if (resp.getAdditionalProperties().get("code").getClass() == Integer.class) {
                code = (Integer) resp.getAdditionalProperties().get("code");
            }
        }
        if (code >= 400 && code < 500) {
            return Response.status(Status.BAD_REQUEST)
                .entity(answer)
                .build();
        }
        else if (code >= 500 && code < 600) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(answer)
                .build();
        }
        else {
            return Response.ok()
                .entity(answer)
                .build();
        }
    }
    
    @Path("/policy/{id}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePolicy(
            @PathParam("id") String name,
            @FormParam("description") String description,
            @FormParam("active") Boolean active,
            @FormParam("groups[]") ArrayList<String> grs,
            @FormParam("nogr") Boolean nogr,
            @FormParam("resources[]") ArrayList<String> res,
            @FormParam("nores") Boolean nores,
            @FormParam("actions[DELETE]") Boolean delete,
            @FormParam("actions[GET]") Boolean get,
            @FormParam("actions[HEAD]") Boolean head,
            @FormParam("actions[OPTIONS]") Boolean options,
            @FormParam("actions[PATCH]") Boolean patch,
            @FormParam("actions[POST]") Boolean post,
            @FormParam("actions[PUT]") Boolean put,
            @FormParam("actions[RETRIEVE]") Boolean retrieve,
            @FormParam("actions[STORE]") Boolean store,
            @FormParam("noact") Boolean noact,
            @HeaderParam("Cookie") String cookie) {
        int code;
        StringBuilder answer = new StringBuilder();
        Policy policy = new Policy();
        ArrayList<Action> actions = new ArrayList<Action>();
        boolean result;
        
        if (delete != null) {
            actions.add(new Action("DELETE", delete.booleanValue()));
        }
        if (get != null) {
            actions.add(new Action("GET", get.booleanValue()));
        }
        if (head != null) {
            actions.add(new Action("HEAD", head.booleanValue()));
        }
        if (options != null) {
            actions.add(new Action("OPTIONS", options.booleanValue()));
        }
        if (patch != null) {
            actions.add(new Action("PATCH", patch.booleanValue()));
        }
        if (post != null) {
            actions.add(new Action("POST", post.booleanValue()));
        }
        if (put != null) {
            actions.add(new Action("PUT", put.booleanValue()));
        }
        if (retrieve != null) {
            actions.add(new Action("RETRIEVE", retrieve.booleanValue()));
        }
        if (store != null) {
            actions.add(new Action("STORE", store.booleanValue()));
        }
        
        String token = null;
        if (cookie != null)
            token = cookie.replaceAll(".*ssoToken=([^;]*).*", "$1");
        
        code = 0;

        ArrayList<String> resCor = new ArrayList<String>();
        for (int i = 0; i < res.size(); i++) {
            resCor.add(UTF8fixer.convert(res.get(i)));
        }

        ArrayList<String> grsCor = new ArrayList<String>();
        for (int i = 0; i < grs.size(); i++) {
            grsCor.add(UTF8fixer.convert(grs.get(i)));
        }
        
        if (client.getPolicy(UTF8fixer.convert(name), token).getSubject().getType().equals("Identity")) {
            result = client.updatePolicyIdentity(UTF8fixer.convert(name), UTF8fixer.convert(description), active, grsCor, nogr, resCor, nores, actions, noact, answer, token);
        } else {
            result = client.updatePolicyAuthenticated(UTF8fixer.convert(name), UTF8fixer.convert(description), active, grsCor, nogr, resCor, nores, actions, noact, answer, token);
        }
        if (result) {
            try {
                policy = (Policy) JsonUtils.deserializeJson(answer.toString(), Policy.class);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            if (policy.getAdditionalProperties().containsKey("code")) {
                if (policy.getAdditionalProperties().get("code").getClass() == Integer.class) {
                    code = (Integer) policy.getAdditionalProperties().get("code");
                }
            }
            if (code >= 400 && code < 500) {
                return Response.status(Status.BAD_REQUEST)
                    .entity(answer.toString())
                    .build();
            }
            else if (code >= 500 && code < 600) {
                return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(answer.toString())
                    .build();
            }
            else {
                return Response.ok()
                    .entity(answer.toString())
                    .build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(answer.toString())
                .build();
        }
    }
    
    @Path("/application/{id}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateApplication(
            @PathParam("id") String name,
            @FormParam("type") String type,
            @FormParam("description") String description,
            @FormParam("resources[]") ArrayList<String> res,
            @FormParam("nores") Boolean nores,
            @FormParam("actions[DELETE]") Boolean delete,
            @FormParam("actions[GET]") Boolean get,
            @FormParam("actions[HEAD]") Boolean head,
            @FormParam("actions[OPTIONS]") Boolean options,
            @FormParam("actions[PATCH]") Boolean patch,
            @FormParam("actions[POST]") Boolean post,
            @FormParam("actions[PUT]") Boolean put,
            @FormParam("actions[RETRIEVE]") Boolean retrieve,
            @FormParam("actions[STORE]") Boolean store,
            @FormParam("noact") Boolean noact,
            @HeaderParam("Cookie") String cookie) {
        int code;
        StringBuilder answer = new StringBuilder();
        Application application = new Application();

        //System.out.println(name);
        
        ArrayList<Action> actions = new ArrayList<Action>();
        
        if (delete != null) {
            actions.add(new Action("DELETE", delete.booleanValue()));
        }
        if (get != null) {
            actions.add(new Action("GET", get.booleanValue()));
        }
        if (head != null) {
            actions.add(new Action("HEAD", head.booleanValue()));
        }
        if (options != null) {
            actions.add(new Action("OPTIONS", options.booleanValue()));
        }
        if (patch != null) {
            actions.add(new Action("PATCH", patch.booleanValue()));
        }
        if (post != null) {
            actions.add(new Action("POST", post.booleanValue()));
        }
        if (put != null) {
            actions.add(new Action("PUT", put.booleanValue()));
        }
        if (retrieve != null) {
            actions.add(new Action("RETRIEVE", retrieve.booleanValue()));
        }
        if (store != null) {
            actions.add(new Action("STORE", store.booleanValue()));
        }
        
        String token = null;
        if (cookie != null)
            token = cookie.replaceAll(".*ssoToken=([^;]*).*", "$1");
        
        code = 0;

        ArrayList<String> resCor = new ArrayList<String>();
        for (int i = 0; i < res.size(); i++) {
            resCor.add(UTF8fixer.convert(res.get(i)));
        }

        if (client.updateApplication(UTF8fixer.convert(type), UTF8fixer.convert(name), UTF8fixer.convert(description), resCor, nores, actions, noact, answer, token)) {
            try {
                application = (Application) JsonUtils.deserializeJson(answer.toString(), Application.class);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            if (application.getAdditionalProperties().containsKey("code")) {
                if (application.getAdditionalProperties().get("code").getClass() == Integer.class) {
                    code = (Integer) application.getAdditionalProperties().get("code");
                }
            }
            if (code >= 400 && code < 500) {
                return Response.status(Status.BAD_REQUEST)
                    .entity(answer.toString())
                    .build();
            }
            else if (code >= 500 && code < 600) {
                return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(answer.toString())
                    .build();
            }
            else {
                return Response.ok()
                    .entity(answer.toString())
                    .build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(answer.toString())
                .build();
        }
    }
    
    @Path("/authenticate")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(
            @FormParam("name") String name,
            @FormParam("password") String password,
            @FormParam("testCookie") boolean altCookie,
            @Context UriInfo uri) {
        Authenticate auth;
        String answer;
        int code;
        
        if (name == null || password == null) {
            return Response.status(Status.BAD_REQUEST)
                .build();
        }

        answer = null;
        code = 0;
        auth = client.authenticate(UTF8fixer.convert(name), UTF8fixer.convert(password));
        
        try {
            answer = JsonUtils.serializeJson(auth);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (auth.getAdditionalProperties().containsKey("code")) {
            if (auth.getAdditionalProperties().get("code").getClass() == Integer.class) {
                code = (Integer) auth.getAdditionalProperties().get("code");
            }
        }
        if (code >= 400 && code < 500) {
            return Response.status(Status.BAD_REQUEST)
                .entity(answer)
                .build();
        }
        else if (code >= 500 && code < 600) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(answer)
                .build();
        }
        else {
            Cookie ck;
            
            // Let's give back some info about the user
            User user = client.getUser(UTF8fixer.convert(name), auth.getTokenId()); // get the info
            AuthenticationResponse resp = new AuthenticationResponse();
            resp.setUid(UTF8fixer.convert(name)); // always give back the login username
            List<String> gn = null;
            gn = user.getGivenName();
            if ((gn != null) && (!gn.isEmpty())) { // send back the first name if available
                resp.setName(gn.get(0));
            } else {
                gn = user.getGivenname();
                if ((gn != null) && (!gn.isEmpty())) {
                    resp.setName(gn.get(0));
                }
            }
            List<String> ln = null;
            ln = user.getSn();
            if ((gn != null) && (!gn.isEmpty()) && (ln != null) && (!ln.isEmpty())) { // send back the full name if available
                resp.setFullname(gn.get(0) + " " + ln.get(0)); // composed by first name + last name
            } else { // otherwise use common name, but it is not the full name for sure
                List<String> cn = null;
                cn = user.getCn();
                if ((cn != null) && (!cn.isEmpty())) {
                    resp.setFullname(cn.get(0));
                }
            }
            
            List<String> mail = null;
            mail = user.getMail();
            if ((mail != null) && (!mail.isEmpty())) {
                resp.setMailhash(MD5Util.md5Hex(mail.get(0)));
            }
            
            List<String> time = user.getCreateTimestamp();
            SimpleDate date = new SimpleDate();
            if ((time != null) && (!time.isEmpty())) {
                date.setYear(time.get(0).substring(0, 4));
                int m = Integer.parseInt(time.get(0).substring(4, 6));
                date.setMonth(new DateFormatSymbols().getMonths()[m - 1]);
                date.setDay(time.get(0).substring(6, 8));
                resp.setCreation(date);
            }

            try {
                answer = JsonUtils.serializeJson(resp);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            // Will all services be over HTTPS? Will any client side script need to access the cookies?
            // If the answer is (Yes, No) then we can keep secure and HttpOnly flags
            String domain = uri.getBaseUri().getHost();
            Pattern pattern = Pattern.compile("^[^.]*(..*)$");
            Matcher matcher = pattern.matcher(domain);
            if (matcher.find()) {
                domain = matcher.group(1);
            }

            if (!altCookie) {
                ck = new Cookie(client.getSSOCookieName(), auth.getTokenId(), "/", domain);
                return Response.ok()
                    .entity(answer)
                    .header("SET-COOKIE", ck.toString() + "; secure" + "; HttpOnly")
                    .build();
            } else {
                ck = new Cookie(client.getAltCookieName(), auth.getTokenId(), "/", domain);
                return Response.ok()
                    .entity(answer)
                    .header("SET-COOKIE", ck.toString() + "; secure" + "; HttpOnly")
                    .build();
            }
        }
    }
    
    @Path("/logout")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(
            @HeaderParam("Cookie") String cookie,
            @FormParam("testCookie") boolean altCookie,
            @Context UriInfo uri) {
        LogoutResponse resp;
        String answer;
        int code;
        
        String altToken = null, ssoToken = null;
        if (cookie != null) {
            altToken = cookie.replaceAll(".*altToken=([^;]*).*", "$1");
            ssoToken = cookie.replaceAll(".*ssoToken=([^;]*).*", "$1");
        }
        
        answer = null;
        code = 0;
        if (altCookie) {
            resp = client.logout(altToken);
        }
        else {
            resp = client.logout(ssoToken);
        }
        
        try {
            answer = JsonUtils.serializeJson(resp);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (resp.getAdditionalProperties().containsKey("code")) {
            if (resp.getAdditionalProperties().get("code").getClass() == Integer.class) {
                code = (Integer) resp.getAdditionalProperties().get("code");
            }
        }
        if (code >= 400 && code < 500) {
            return Response.status(Status.BAD_REQUEST)
                .entity(answer)
                .build();
        }
        else if (code >= 500 && code < 600) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(answer)
                .build();
        }
        else {
            Cookie ck;
            
            String domain = uri.getBaseUri().getHost();
            Pattern pattern = Pattern.compile("^[^.]*(..*)$");
            Matcher matcher = pattern.matcher(domain);
            if (matcher.find()) {
                domain = matcher.group(1);
            }
            
            if (!altCookie) {
                ck = new Cookie(client.getSSOCookieName(), "", "/", domain);
                return Response.ok()
                    .entity(answer)
                    .header("SET-COOKIE", ck.toString() + "; secure" + "; HttpOnly")
                    .build();
            } else {
                ck = new Cookie(client.getAltCookieName(), "", "/", domain);
                return Response.ok()
                    .entity(answer)
                    .header("SET-COOKIE", ck.toString() + "; secure" + "; HttpOnly")
                    .build();
            }
        }
    }
    
    @Path("/evaluate")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response evaluate(
            @HeaderParam("Cookie") String cookie,
            @FormParam("testCookie") boolean altCookie,
            @FormParam("resources[]") ArrayList<String> res) {
        int code;
        String tokenPerformer, tokenUser;
        StringBuilder answer = new StringBuilder();
        DecisionArray resp = new DecisionArray();
        
        String altToken = null, ssoToken = null;
        if (cookie != null) {
            altToken = cookie.replaceAll(".*altToken=([^;]*).*", "$1");
            ssoToken = cookie.replaceAll(".*ssoToken=([^;]*).*", "$1");
        }
        
        code = 0;
        if (altCookie) {
            tokenPerformer = ssoToken;
            tokenUser = altToken;
        } else {
            tokenPerformer = altToken;
            tokenUser = ssoToken;
        }

        ArrayList<String> resCor = new ArrayList<String>();
        for (int i = 0; i < res.size(); i++) {
            resCor.add(UTF8fixer.convert(res.get(i)));
        } 
        
        if (client.evaluate(tokenUser, resCor, answer, tokenPerformer)) {
            try {
                resp = (DecisionArray) JsonUtils.deserializeJson(answer.toString(), DecisionArray.class);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            if (resp.getAdditionalProperties().containsKey("code")) {
                if (resp.getAdditionalProperties().get("code").getClass() == Integer.class) {
                    code = (Integer) resp.getAdditionalProperties().get("code");
                }
            }
            if (code >= 400 && code < 500) {
                return Response.status(Status.BAD_REQUEST)
                    .entity(answer.toString())
                    .build();
            }
            else if (code >= 500 && code < 600) {
                return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(answer.toString())
                    .build();
            }
            else {
                return Response.ok()
                    .entity(answer.toString())
                    .build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(answer.toString())
                .build();
        }
    }
    
    @Path("/user/register")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(
            @FormParam("mail") String mail) {
        GenericObject resp;
        String answer;
        int code;
        
        answer = null;
        code = 0;
        resp = client.register(UTF8fixer.convert(mail));
        
        try {
            answer = JsonUtils.serializeJson(resp);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (resp.getAdditionalProperties().containsKey("code")) {
            if (resp.getAdditionalProperties().get("code").getClass() == Integer.class) {
                code = (Integer) resp.getAdditionalProperties().get("code");
            }
        }
        if (code >= 400 && code < 500) {
            return Response.status(Status.BAD_REQUEST)
                .entity(answer)
                .build();
        }
        else if (code >= 500 && code < 600) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(answer)
                .build();
        }
        else {
            return Response.ok()
                .entity(answer)
                .build();
        }
    }
    
    @Path("/user/signup")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response signupUser(
            @FormParam("givenName") String givenName,
            @FormParam("surname") String surname,
            @FormParam("name") String username,
            @FormParam("password") String password,
            @FormParam("mail") String mail,
            @FormParam("tokenId") @Encoded String tokenId,
            @FormParam("confirmationId") @Encoded String confirmationId) {
        GenericObject resp;
        String answer;
        int code;
        String tokenDec = "";
        String confirmDec = "";
        
        try {
            tokenDec = URLDecoder.decode(tokenId.replace("+", "%2B"), "UTF-8").replace("%2B", "+");
            confirmDec = URLDecoder.decode(confirmationId.replace("+", "%2B"), "UTF-8").replace("%2B", "+");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        
        answer = null;
        code = 0;
        resp = client.confirm(UTF8fixer.convert(mail), tokenDec, confirmDec);
        
        try {
            answer = JsonUtils.serializeJson(resp);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (resp.getAdditionalProperties().containsKey("code")) {
            if (resp.getAdditionalProperties().get("code").getClass() == Integer.class) {
                code = (Integer) resp.getAdditionalProperties().get("code");
            }
        }
        if (code >= 400 && code < 500) {
            return Response.status(Status.BAD_REQUEST)
                .entity(answer)
                .build();
        }
        else if (code >= 500 && code < 600) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(answer)
                .build();
        }
        else {
            answer = null;
            code = 0;
            resp = client.selfCreateUser(UTF8fixer.convert(mail), tokenDec, confirmDec, UTF8fixer.convert(username), UTF8fixer.convert(password));
            
            try {
                answer = JsonUtils.serializeJson(resp);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            if (resp.getAdditionalProperties().containsKey("code")) {
                if (resp.getAdditionalProperties().get("code").getClass() == Integer.class) {
                    code = (Integer) resp.getAdditionalProperties().get("code");
                }
            }
            if (code >= 400 && code < 500) {
                return Response.status(Status.BAD_REQUEST)
                    .entity(answer)
                    .build();
            }
            else if (code >= 500 && code < 600) {
                return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(answer)
                    .build();
            }
            else {
                Authenticate auth;
                
                answer = null;
                code = 0;
                auth = client.authenticate(UTF8fixer.convert(username), UTF8fixer.convert(password));
                
                try {
                    answer = JsonUtils.serializeJson(auth);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                if (auth.getAdditionalProperties().containsKey("code")) {
                    if (auth.getAdditionalProperties().get("code").getClass() == Integer.class) {
                        code = (Integer) auth.getAdditionalProperties().get("code");
                    }
                }
                if (code >= 400 && code < 500) {
                    return Response.status(Status.BAD_REQUEST)
                        .entity(answer)
                        .build();
                }
                else if (code >= 500 && code < 600) {
                    return Response.status(Status.INTERNAL_SERVER_ERROR)
                        .entity(answer)
                        .build();
                }
                else {
                    StringBuilder answer1 = new StringBuilder();
                    String userJson = null;
                    User user = new User();
                    
                    code = 0;
                    
                    if (client.updateUser(UTF8fixer.convert(username), UTF8fixer.convert(givenName), UTF8fixer.convert(surname), UTF8fixer.convert(mail), "Active", answer1, auth.getTokenId())) {
                        try {
                            user = (User) JsonUtils.deserializeJson(answer1.toString(), User.class);
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        
                        if (user.getAdditionalProperties().containsKey("code")) {
                            if (user.getAdditionalProperties().get("code").getClass() == Integer.class) {
                                code = (Integer) user.getAdditionalProperties().get("code");
                            }
                        }
                        if (code >= 400 && code < 500) {
                            return Response.status(Status.BAD_REQUEST)
                                .entity(answer1.toString())
                                .build();
                        }
                        else if (code >= 500 && code < 600) {
                            return Response.status(Status.INTERNAL_SERVER_ERROR)
                                .entity(answer1.toString())
                                .build();
                        }
                        else {
                            List<String> gn = null;
                            gn = user.getGivenName();
                            if ((gn != null) && (!gn.isEmpty())) { // send back the first name if available
                                if (gn.get(0).equals(" "))
                                    user.setGivenName(null);
                            }
                            
                            gn = user.getGivenname();
                            if ((gn != null) && (!gn.isEmpty())) {
                                if (gn.get(0).equals(" "))
                                    user.setGivenName(null);
                            }
                            try {
                                userJson = JsonUtils.serializeJson(user);
                            } catch (JsonParseException e) {
                                e.printStackTrace();
                            } catch (JsonMappingException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            client.logout(auth.getTokenId());
                            return Response.ok()
                                .entity(userJson)
                                .build();
                        }
                    } else {
                        return Response.status(Status.INTERNAL_SERVER_ERROR)
                            .entity(answer1.toString())
                            .build();
                    }
                }
            }
        }
    }
}

