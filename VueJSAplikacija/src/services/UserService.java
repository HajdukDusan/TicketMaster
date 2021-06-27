package services;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.User;
import dao.CardDAO;
import dao.ManifestationDAO;
import dao.UserDAO;
@Path("/user")  
public class UserService {  
	@Context
	HttpServletRequest request;
	@Context
	ServletContext ctx;
	
	@PostConstruct
	public void init() {
		if (ctx.getAttribute("userDAO") == null) {
			String path = ctx.getRealPath("/") + "data\\manifestations.txt";
			String path1 = ctx.getRealPath("/")+ "data\\cards.txt";
			String path2 = ctx.getRealPath("/")+ "data\\users.txt";
			ManifestationDAO mDAO = new ManifestationDAO(path);
			CardDAO cDAO = new CardDAO(path1,mDAO);
			ctx.setAttribute("userDAO", new UserDAO(path2,mDAO,cDAO));
		}
	}
	
	
	
	@GET  
	@Produces(MediaType.TEXT_PLAIN)  
	  public String sayPlainTextHello() {  
	    return "Hello Jersey Plain";  
	  }  
	@GET 
	@Path("/userInfo/{cookie}")
	@Produces(MediaType.APPLICATION_JSON)  
	  public Response getUserInfo(@PathParam(value = "cookie") String cookie) {
		UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
		String username = cookie.split("-")[0];
		System.out.println(username);
		System.out.println(dao.getUsers());
		if(dao.findUser(username) == null) {
			return Response.status(Response.Status.NOT_FOUND).entity("Bad cookie value").build();
		}
		return Response.ok(dao.findUser(cookie.split("-")[0]), MediaType.APPLICATION_JSON).build();
	  } 
	@GET
	@Path("/users")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<User> getUsers(){
		UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
		return dao.getUsersList();
	}
	@POST
	@Path("/registerUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerUser(User userToRegister){
		UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
		if(dao.findUser(userToRegister.getUsername()) != null) {
			return Response.status(Response.Status.NOT_FOUND).entity("Username already used!").build();
		}
		dao.save(userToRegister);
		return Response.ok(MediaType.APPLICATION_JSON).build();
	}
	@PUT
	@Path("/updateUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateUser(User userToRegister){
		UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
		if(dao.findUser(userToRegister.getUsername()) != null) {
			dao.save(userToRegister);
			return Response.ok(MediaType.APPLICATION_JSON).build();
			
		}
		return Response.status(Response.Status.NOT_FOUND).entity("User not found!").build();
		
	}
	@GET
	@Path("/loginUser/{username}/{password}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response loginUser(@PathParam("username") String username,@PathParam("password")  String password){
		UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
		for(User u: dao.getUsersList()) {
			System.out.println(u);
		}
		if(dao.loginUser(username,password) == null) {
			 return Response.status(Response.Status.NOT_FOUND).entity("Wrong username or password!").build();
		}
		 return Response.ok(username+"-"+password+ "." + dao.findUser(username).getRole(), MediaType.APPLICATION_JSON).build();
	}
}   