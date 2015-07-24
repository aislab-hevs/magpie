package ch.hevs.aislab.magpie.debs.retrofit;


import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class UnsafeHttpsClient {

    public static HttpClient createUnsafeClient() {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    builder.build());
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf).build();
            return httpclient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
