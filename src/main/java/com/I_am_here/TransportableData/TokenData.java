package com.I_am_here.TransportableData;


import java.io.Serializable;
import java.util.Date;

public class TokenData implements Serializable {

    private static final long serialVersionUID = -1898384066498866625L;
    private String access_token;

    private String refresh_token;

    private Date access_token_expire_date;

    private Date refresh_token_expire_date;

    public TokenData(String access_token, String refresh_token, Date access_token_expire_date, Date refresh_token_expire_date) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.access_token_expire_date = access_token_expire_date;
        this.refresh_token_expire_date = refresh_token_expire_date;
    }

    public TokenData() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TokenData tokenData = (TokenData) o;

        if (!access_token.equals(tokenData.access_token)) return false;
        if (!refresh_token.equals(tokenData.refresh_token)) return false;
        if (!access_token_expire_date.equals(tokenData.access_token_expire_date)) return false;
        return refresh_token_expire_date.equals(tokenData.refresh_token_expire_date);

    }

    @Override
    public int hashCode() {
        int result = access_token.hashCode();
        result = 31 * result + refresh_token.hashCode();
        result = 31 * result + access_token_expire_date.hashCode();
        result = 31 * result + refresh_token_expire_date.hashCode();
        return result;
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

    public Date getAccess_token_expire_date() {
        return access_token_expire_date;
    }

    public void setAccess_token_expire_date(Date access_token_expire_date) {
        this.access_token_expire_date = access_token_expire_date;
    }

    public Date getRefresh_token_expire_date() {
        return refresh_token_expire_date;
    }

    public void setRefresh_token_expire_date(Date refresh_token_expire_date) {
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
