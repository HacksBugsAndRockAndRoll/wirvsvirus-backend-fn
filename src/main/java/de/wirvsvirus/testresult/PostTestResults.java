package de.wirvsvirus.testresult;

import java.util.Optional;

import org.bson.Document;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class PostTestResults {

	@FunctionName("tests-eu")
	public HttpResponseMessage run(@HttpTrigger(name = "req", methods = {
			HttpMethod.POST }, authLevel = AuthorizationLevel.FUNCTION, route = "tests/{hash}") HttpRequestMessage<Optional<String>> request,
			@BindingName("hash") String hash, final ExecutionContext context) {
		context.getLogger().info("Java HTTP trigger processed a request.");
		if (hash == null) {
			return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("no hash given").build();
		}
		Optional<HttpResponseMessage> msg = request.getBody().map(b -> {
			String uri = System.getenv("mongodb_uri");
			if (uri == null) {
				uri = "mongodb://localhost=27017/fn-test";
			}
			try (MongoClient client = new MongoClient(new MongoClientURI(uri))) {
				BasicDBObject o = BasicDBObject.parse(b);
				o.put("_id", hash);
				client.getDatabase("fn-test").getCollection("testresult").insertOne(Document.parse(o.toJson()));
				;
				return request.createResponseBuilder(HttpStatus.OK).body(o).build();
			} catch (Exception e) {
				return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()).build();
			}
		});
		return msg.orElseGet(() -> request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build());
	}
}
