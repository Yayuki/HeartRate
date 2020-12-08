package yayuki.heartrate.server.repository;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

public enum ChannelRepository {
    INSTANCE;
    private final ConcurrentHashMap<Integer, Channel> carerMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Channel> patientMap = new ConcurrentHashMap<>();

    public Channel getCarerChannel(int id) {
        return carerMap.get(id);
    }

    public Channel getPatientChannel(int id) {
        return patientMap.get(id);
    }

    public void addCarer(int id, Channel channel) {
        carerMap.put(id, channel);
    }

    public void addPatient(int id, Channel channel) {
        patientMap.put(id, channel);
    }

    public void removeCarer(int id) {
        carerMap.remove(id);
    }

    public void removePatient(int id) {
        patientMap.remove(id);
    }
}
