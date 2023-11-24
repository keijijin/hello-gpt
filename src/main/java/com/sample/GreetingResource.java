package com.sample;

import com.hexadevlabs.gpt4all.LLModel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Map;

@Path("/interact")
public class GreetingResource {

    @ConfigProperty(name = "model.path")
    String baseModelPath;

    @ConfigProperty(name = "model.file")
    String modelFilePath;

    LLModel model;
    LLModel.GenerationConfig config;

    @PostConstruct
    public void initModel() {
        java.nio.file.Path modelPath = java.nio.file.Path.of(baseModelPath, modelFilePath);

        model = new LLModel(modelPath);
        config = LLModel.config()
                .withNPredict(4096).build();
    }

    @PreDestroy
    public void clearModel() throws Exception {
        model.close();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }


    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(String content) {
        final List<Map<String, String>> message = List.of(createMessage(content));

        final LLModel.ChatCompletionResponse chatCompletionResponse = model.chatCompletion(message, config);

        return chatCompletionResponse.choices.toString();
    }

    private Map<String, String> createMessage(String content) {
        return Map.of("role", "user", "content", content);
    }

}
