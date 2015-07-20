package ch.hevs.aislab.magpie.publisher.retrofit;


import java.net.ConnectException;

import ch.hevs.aislab.magpie.publisher.model.Publisher;
import retrofit.http.Body;
import retrofit.http.POST;

public interface PublisherSvcApi {

    String PUB_SVC_PATH = "/publisher";
    String ADD_PUB_SVC_PATH = PUB_SVC_PATH + "/new";

    @POST(ADD_PUB_SVC_PATH)
    Publisher addPublisher(@Body Publisher p) throws ConnectException;


}
