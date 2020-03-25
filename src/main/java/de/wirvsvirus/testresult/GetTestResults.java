package de.wirvsvirus.testresult;

import java.util.Optional;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.conversions.Bson;

import com.fasterxml.jackson.databind.node.TextNode;
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

public class GetTestResults {

	@FunctionName("tests")
	public HttpResponseMessage run(@HttpTrigger(name = "req", methods = {
			HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS, route = "/{hash:alpha}") HttpRequestMessage<Optional<String>> request,
			@BindingName("hash") String hash, final ExecutionContext context) {
		context.getLogger().info("Java HTTP trigger processed a request.");

		if (hash == null) {
			return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("no hash given").build();
		} else {
			String uri = System.getenv("mongodb.uri");
			if (uri==null)
				uri="mongodb://localhost=27017/fn-test";
			try (MongoClient client = new MongoClient(uri)) {
				Bson filter = new BsonDocument("_id", new BsonString(hash));
				TestResult res = client.getDatabase("fn-test").getCollection("testresult").find(filter, TestResult.class).first();
				return request.createResponseBuilder(HttpStatus.OK).body(res).build();
			}
		}
	}
}
