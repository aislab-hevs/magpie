package ch.hevs.aislab.magpie.debs.retrofit;


public class SecuredRestException extends RuntimeException {

    public SecuredRestException() {
        super();
    }

    public SecuredRestException(String detailMessage) {
        super(detailMessage);
    }

    public SecuredRestException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public SecuredRestException(Throwable throwable) {
        super(throwable);
    }

}
