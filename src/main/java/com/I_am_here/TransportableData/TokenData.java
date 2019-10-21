package com.I_am_here.TransportableData;


import java.io.Serializable;
import java.util.Date;


/**
 This class is used to transfer token data from server to client.
 It is used in controllers. Controller methods often return ResponseEntity, contain
 TokenData, HttpStatus and other data.
 Jackson library automatically converts this objects to json format
 like this:
 {
 access_token: "dfasjrh1k2h3321312",
 refresh_token: "jgkfj3j235jkfudhw",
 access_token_expire_date: "423218372",
 refresh_token_expire_date: "4231348348"
 }

 */
public class TokenData implements Serializable {



    private static final long serialVersionUID = -1898384066498866625L;
    private String access_token;

    private String refresh_token;

    private long access_token_expire_date;

    private long refresh_token_expire_date;

    public TokenData(String access_token, String refresh_token, Date access_token_expire_date, Date refresh_token_expire_date) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.access_token_expire_date = access_token_expire_date.getTime();
        this.refresh_token_expire_date = refresh_token_expire_date.getTime();
    }

    public TokenData() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TokenData tokenData = (TokenData) o;

        if (access_token_expire_date != tokenData.access_token_expire_date) return false;
        if (refresh_token_expire_date != tokenData.refresh_token_expire_date) return false;
        if (access_token != null ? !access_token.equals(tokenData.access_token) : tokenData.access_token != null)
            return false;
        return refresh_token != null ? refresh_token.equals(tokenData.refresh_token) : tokenData.refresh_token == null;

    }

    @Override
    public int hashCode() {
        int result = access_token != null ? access_token.hashCode() : 0;
        result = 31 * result + (refresh_token != null ? refresh_token.hashCode() : 0);
        result = 31 * result + (int) (access_token_expire_date ^ (access_token_expire_date >>> 32));
        result = 31 * result + (int) (refresh_token_expire_date ^ (refresh_token_expire_date >>> 32));
        return result;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public long getAccess_token_expire_date() {
        return access_token_expire_date;
    }

    public void setAccess_token_expire_date(long access_token_expire_date) {
        this.access_token_expire_date = access_token_expire_date;
    }

    public long getRefresh_token_expire_date() {
        return refresh_token_expire_date;
    }

    public void setRefresh_token_expire_date(long refresh_token_expire_date) {
        this.refresh_token_expire_date = refresh_token_expire_date;
    }

    @Override
    public String toString() {
        return "TokenData{" +
                "access_token='" + access_token + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", access_token_expire_date=" + access_token_expire_date +
                ", refresh_token_expire_date=" + refresh_token_expire_date +
                '}';
    }
}
