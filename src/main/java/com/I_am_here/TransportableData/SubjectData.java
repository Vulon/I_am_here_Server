package com.I_am_here.TransportableData;

import com.I_am_here.Database.Entity.Subject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class SubjectData implements Serializable {

    private static final long serialVersionUID = -3338923437221723900L;
    private Integer id;

    private String name;

    private Integer plan;

    private String description;

    private long start_date;

    private long finish_date;

    private String code;

    private ArrayList<HashMap<String, String>> hosts;

    private ArrayList<HashMap<String, String>> parties;

    public SubjectData() {
    }

    public SubjectData(Subject subject){
        id = subject.getSubjectId();
        name = subject.getName();
        plan = subject.getPlan();
        description = subject.getDescription();
        start_date = subject.getStartDate().getTime();
        finish_date = subject.getFinishDate().getTime();
        code = subject.getBroadcastWord();

        hosts = new ArrayList<>();
        subject.getHosts().forEach(host -> {
            HashMap<String, String> map = new HashMap<>(2);
            map.put("id", host.getHostId().toString());
            map.put("name", host.getName());
            hosts.add(map);
        });

        parties = new ArrayList<>();
        subject.getParties().forEach(party -> {
            HashMap<String, String> map = new HashMap<>(2);
            map.put("id", party.getParty().toString());
            map.put("name", party.getName());
            hosts.add(map);
        });
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubjectData that = (SubjectData) o;

        if (start_date != that.start_date) return false;
        if (finish_date != that.finish_date) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (plan != null ? !plan.equals(that.plan) : that.plan != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (hosts != null ? !hosts.equals(that.hosts) : that.hosts != null) return false;
        return parties != null ? parties.equals(that.parties) : that.parties == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (plan != null ? plan.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) (start_date ^ (start_date >>> 32));
        result = 31 * result + (int) (finish_date ^ (finish_date >>> 32));
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (hosts != null ? hosts.hashCode() : 0);
        result = 31 * result + (parties != null ? parties.hashCode() : 0);
        return result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPlan() {
        return plan;
    }

    public void setPlan(Integer plan) {
        this.plan = plan;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getStart_date() {
        return start_date;
    }

    public void setStart_date(long start_date) {
        this.start_date = start_date;
    }

    public long getFinish_date() {
        return finish_date;
    }

    public void setFinish_date(long finish_date) {
        this.finish_date = finish_date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<HashMap<String, String>> getHosts() {
        return hosts;
    }

    public void setHosts(ArrayList<HashMap<String, String>> hosts) {
        this.hosts = hosts;
    }

    public ArrayList<HashMap<String, String>> getParties() {
        return parties;
    }

    public void setParties(ArrayList<HashMap<String, String>> parties) {
        this.parties = parties;
    }

    @Override
    public String toString() {
        return "SubjectData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", plan=" + plan +
                ", description='" + description + '\'' +
                ", start_date=" + start_date +
                ", finish_date=" + finish_date +
                ", code='" + code + '\'' +
                ", hosts=" + hosts +
                ", parties=" + parties +
                '}';
    }
}
