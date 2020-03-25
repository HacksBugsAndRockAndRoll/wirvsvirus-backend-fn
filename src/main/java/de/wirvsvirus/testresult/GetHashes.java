package de.wirvsvirus.testresult;

import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.format.DateTimeFormat;

import com.fasterxml.jackson.databind.node.TextNode;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class GetHashes {

	@FunctionName("hashes")
	public HttpResponseMessage run(
			@HttpTrigger(name = "req", methods = {HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS) 
			HttpRequestMessage<Optional<String>> request,
			final ExecutionContext context) {
		context.getLogger().info("Java HTTP trigger processed a request.");
		String sampleId = request.getQueryParameters().getOrDefault("sampleId", null);
		String name = request.getQueryParameters().getOrDefault("name", null);
		String birthDay = request.getQueryParameters().getOrDefault("birthDay", null);
		if (sampleId == null || name == null || birthDay == null) {
			return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
					.body("incomplete parameterset please provide sampleId,name and birthDay").build();
		} else {
			String hash = DigestUtils.sha256Hex(sampleId + name + DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate(birthDay).toString());
			return request.createResponseBuilder(HttpStatus.OK).body(TextNode.valueOf(hash)).build();

		}
	}
}
