package wallOfTweets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;



@SuppressWarnings("serial")
public class WallServlet extends HttpServlet {

	private String TWEETS_URI = "/waslab03/tweets/";

	@Override
	// Implements GET http://localhost:8080/waslab03/tweets
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		resp.setContentType("application/json");
		resp.setHeader("Cache-control", "no-cache");
		List<Tweet> tweets= Database.getTweets();
		JSONArray job = new JSONArray();
		for (Tweet t: tweets) {
			JSONObject jt = new JSONObject(t);
			jt.remove("class");
			job.put(jt);
		}
		resp.getWriter().println(job.toString());
	}

	@Override
	// Implements POST http://localhost:8080/waslab03/tweets/:id/likes
	//        and POST http://localhost:8080/waslab03/tweets
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String uri = req.getRequestURI();
		int lastIndex = uri.lastIndexOf("/likes");
		if (lastIndex > -1) {  // uri ends with "/likes"
			// Implements POST http://localhost:8080/waslab03/tweets/:id/likes
			long id = Long.valueOf(uri.substring(TWEETS_URI.length(),lastIndex));		
			resp.setContentType("text/plain");
			resp.getWriter().println(Database.likeTweet(id));
		}
		else { 
			// Implements POST http://localhost:8080/waslab03/tweets
			int max_length_of_data = req.getContentLength();
			byte[] httpInData = new byte[max_length_of_data];
			ServletInputStream  httpIn  = req.getInputStream();
			httpIn.readLine(httpInData, 0, max_length_of_data);
			String body = new String(httpInData);
			/*      ^
		      The String variable body contains the sent (JSON) Data. 
		      Complete the implementation below.*/
			JSONObject a;
			try {
				a = new JSONObject(body);
				String autor = a.getString("author");
				String texto = a.getString("text");
				Tweet tweet = Database.insertTweet(autor, texto);
				String clau = AESencr.encrypt(tweet.getId().toString());
				JSONObject b = new JSONObject(tweet);
				b.put("clau",clau);
				resp.getWriter().println(b.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	// Implements DELETE http://localhost:8080/waslab03/tweets/:id
	public void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		try{
			String uri = req.getRequestURI();
			int lastIndex = uri.length();
			int firstIndex = "/waslab03/tweets/".length();
			String clau = req.getHeader("clau");
			if(clau!=""){
				String sid = uri.substring(firstIndex,lastIndex);
				if(sid.equals(AESencr.decrypt(clau))){
					long id = Long.valueOf(sid);
					Database.deleteTweet(id);
				}
			}	
		}
			catch(Exception e){
				throw new ServletException(e.getMessage());
			}
	}

}
