package de.wirvsvirus.testresult;

import java.util.Optional;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.conversions.Bson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class GetTestResults {

	@FunctionName("tests")
	public HttpResponseMessage run(@HttpTrigger(name = "req", methods = {
			HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS, route = "tests/{hash}") HttpRequestMessage<Optional<String>> request,
			@BindingName("hash") String hash, final ExecutionContext context) {
		context.getLogger().info("Java HTTP trigger processed a request.");

		if (hash == null) {
			return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("no hash given").build();
		} else {
			String uri = System.getenv("mongodb_uri");
			if (uri==null)
				uri="mongodb://localhost=27017/fn-test";
			try (MongoClient client = new MongoClient(new MongoClientURI(uri))) {
				Bson filter = new BsonDocument("_id", new BsonString(hash));
				TestResult res = new ObjectMapper().readValue(client.getDatabase("fn-test").getCollection("testresult").find(filter).first().toJson(),TestResult.class);
				return request.createResponseBuilder(HttpStatus.OK).body(res).build();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
