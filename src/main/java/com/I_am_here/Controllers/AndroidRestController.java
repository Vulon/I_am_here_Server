package com.I_am_here.Controllers;

import com.I_am_here.Database.Account;
import com.I_am_here.Database.Entity.*;
import com.I_am_here.Database.Repository.*;
import com.I_am_here.Security.TokenParser;
import com.I_am_here.Services.QRParser;
import com.I_am_here.Services.SecretDataLoader;
import com.I_am_here.Services.StatusCodeCreator;
import com.I_am_here.TransportableData.PartyData;
import com.I_am_here.TransportableData.TokenData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;


/**
 * ServerController. Methods of this class get called when server gets requests for urls like /app/*
 * Most of this methods can be called only if a request has a valid access token.
 * This controller is designed to work with Host and Participator accounts.
 */
@RestController
public class AndroidRestController {

    private HostRepository hostRepository;
    private ParticipatorRepository participatorRepository;
    private PartyRepository partyRepository;
    private SubjectRepository subjectRepository;
    private VisitRepository visitRepository;

    private ManagerRepository managerRepository;
    private TokenParser tokenParser;
    private SecretDataLoader secretDataLoader;
    private StatusCodeCreator statusCodeCreator;
    private QRParser qrParser;


    public AndroidRestController(HostRepository hostRepository, ParticipatorRepository participatorRepository, PartyRepository partyRepository, SubjectRepository subjectRepository, VisitRepository visitRepository, ManagerRepository managerRepository, TokenParser tokenParser, SecretDataLoader secretDataLoader, StatusCodeCreator statusCodeCreator, QRParser qrParser) {
        this.hostRepository = hostRepository;
        this.participatorRepository = participatorRepository;
        this.partyRepository = partyRepository;
        this.subjectRepository = subjectRepository;
        this.visitRepository = visitRepository;
        this.managerRepository = managerRepository;
        this.tokenParser = tokenParser;
        this.secretDataLoader = secretDataLoader;
        this.statusCodeCreator = statusCodeCreator;
        this.qrParser = qrParser;
    }

    /**
     * This method creates new entry in database and returns TokenData containing new tokens.
     * If such account already exists, server returns status code 409.
     * If account type is wrong or misspelled, server returns status code 400
     * @param UUID - UUID from FireBase
     * @param password - user password
     * @param account_type - account type (ACCOUNT_HOST, ACCOUNT_PARTICIPATOR)
     * @param name - name. Not necessary
     * @param email - email Not necessary
     */
    @PostMapping("/app/register")
    public ResponseEntity<TokenData> register(
            @RequestHeader String UUID,
            @RequestHeader String password,
            @RequestParam(name = "account_type") String account_type,
            @RequestParam(name = "name", defaultValue = "", required = false) String name,
            @RequestParam(name = "email", defaultValue = "", required = false) String email){
        try{

            TokenParser.ACCOUNT type = TokenParser.ACCOUNT.valueOf(account_type);
            Account account = getAccount(UUID, password, type);
            if(account != null){
                return error(statusCodeCreator.alreadyRegistered());
            }
            Date now = Date.from(Instant.now());

            if(type == TokenParser.ACCOUNT.ACCOUNT_HOST){
                TokenData data = tokenParser.createTokenData(UUID, password, TokenParser.ACCOUNT.ACCOUNT_HOST, now);
                Host host = new Host(UUID, name, email, password, data);
                hostRepository.saveAndFlush(host);
                return new ResponseEntity<>(data, HttpStatus.OK);
            }else if(type == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
                TokenData data = tokenParser.createTokenData(UUID, password, TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR, now);
                Participator participator = new Participator(UUID, name, email, password, data);
                participatorRepository.saveAndFlush(participator);

                return new ResponseEntity<>(data, HttpStatus.OK);
            }else{
                return error(statusCodeCreator.missingAccountTypeField());
            }
        }catch (Exception e){
            e.printStackTrace();
            return error(statusCodeCreator.serverError());
        }
    }


    @PostMapping("/app/login")
    public ResponseEntity<TokenData> login(
            @RequestHeader String UUID,
            @RequestHeader String password,
            @RequestParam String account_type
    ){
        try{

            Date now = Date.from(Instant.now());
            if(account_type.equals(TokenParser.ACCOUNT.ACCOUNT_HOST.name())){
                Host host = hostRepository.findByUuidAndPassword(UUID, password);
                if(host == null){
                    return error(statusCodeCreator.userNotFound());
                }
                TokenData tokenData = tokenParser.createTokenData(host.getUuid(), password, TokenParser.ACCOUNT.ACCOUNT_HOST, now);
                host.setAccessToken(tokenData.getAccess_token());
                host.setRefreshToken(tokenData.getRefresh_token());
                hostRepository.saveAndFlush(host);
                return new ResponseEntity<TokenData>(tokenData, HttpStatus.OK);

            }else if(account_type.equals(TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR.name())){

                Participator participator = participatorRepository.findByUuidAndPassword(UUID, password);
                if(participator == null){
                    return error(statusCodeCreator.userNotFound());
                }
                TokenData tokenData = tokenParser.createTokenData(participator.getUuid(), password, TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR, now);
                participator.setAccessToken(tokenData.getAccess_token());
                participator.setRefreshToken(tokenData.getRefresh_token());
                participatorRepository.saveAndFlush(participator);
                return new ResponseEntity<TokenData>(tokenData, HttpStatus.OK);
            }else{
                return error(statusCodeCreator.missingAccountTypeField());
            }
        }catch (Exception e){
            e.printStackTrace();
            return error(statusCodeCreator.serverError());
        }
    }

    @GetMapping("/check")
    public ResponseEntity<HashMap<String, String>> checkIfRegistered(@RequestHeader String UUID){
        try{
            Host host = hostRepository.getByUuid(UUID);
            Manager manager = managerRepository.getByUuid(UUID);
            Participator participator = participatorRepository.getByUuid(UUID);
            HashMap<String, String> resp = new HashMap<>();
            resp.put("host", (host == null ? "Not found" : "Found"));
            resp.put("manager", (manager == null ? "Not found" : "Found"));
            resp.put("participator", (participator == null ? "Not found" : "Found"));
            return new ResponseEntity<>(resp, HttpStatus.OK);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }



    @GetMapping("/app/refresh")
    public ResponseEntity<TokenData> updateAccessToken(
            @RequestHeader String refresh_token
    ){
        try{
            TokenParser.ACCOUNT account_type = tokenParser.getAccountType(refresh_token);
            TokenParser.TYPE token_type = tokenParser.getType(refresh_token);
            if(token_type != TokenParser.TYPE.REFRESH){
                return error(statusCodeCreator.tokenNotValid());
            }
            String UUID = tokenParser.getUUID(refresh_token);
            String password = tokenParser.getPassword(refresh_token);

            String access_token = tokenParser.createToken(UUID, password, TokenParser.TYPE.ACCESS, Date.from(Instant.now()), account_type);
            if (account_type == TokenParser.ACCOUNT.ACCOUNT_HOST){
                Host host = hostRepository.findByUuidAndPassword(UUID, password);
                if(host == null){
                    return error(statusCodeCreator.userNotFound());
                }
                host.setAccessToken(access_token);
                hostRepository.saveAndFlush(host);
                return new ResponseEntity<>(tokenParser.getTokenData(host), HttpStatus.OK);
            }else if(account_type == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
                Participator participator = participatorRepository.findByUuidAndPassword(UUID, password);
                if(participator == null){
                    return error(statusCodeCreator.userNotFound());
                }
                participator.setAccessToken(access_token);
                participatorRepository.saveAndFlush(participator);
                return new ResponseEntity<>(tokenParser.getTokenData(participator), HttpStatus.OK);
            }else{
                return error(statusCodeCreator.missingAccountTypeField());
            }
        }catch (Exception e){
            e.printStackTrace();
            return error(statusCodeCreator.serverError());
        }
    }

    @GetMapping("/app/participator/find_party")
    public ResponseEntity<Set<PartyData>> getPartyList(
            @RequestHeader String access_token,
            @RequestParam String code_word
    ){
        try{
            Date broadcast_start = Date.from(Instant.now().minusSeconds(secretDataLoader.getPartyBroadcastDuration()));
            Set<Party> partyList = partyRepository.getAllByBroadcastWord(code_word);
            if(partyList == null){
                return new ResponseEntity<>(new HashSet<PartyData>(), HttpStatus.OK);
            }
            return  new ResponseEntity<>(PartyData.createPartyData(partyList), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return  new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @GetMapping("/app/participator/find_parties_by_code_words")
    public ResponseEntity<Set<PartyData>> findPartiesByCodeWords(
            @RequestHeader String access_token
    ){
        try{
            Participator participator = (Participator)getAccount(access_token);
            if(participator == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            Set<Party> parties = partyRepository.getAllByBroadcastWordIn(participator.getCodeWordsStrings());
            return new ResponseEntity<>(PartyData.createPartyData(parties), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @PostMapping("/app/participator/join_party")
    public ResponseEntity<String> joinParty(
            @RequestParam Integer party_id,
            @RequestParam String code_word,
            @RequestHeader String access_token
    ){
        try{
            Party party = partyRepository.findByParty(party_id);
            if(party == null){
                return new ResponseEntity<>("Not found", statusCodeCreator.userNotFound());
            }
            if(party.getBroadcastWord().equals(code_word)){
                Participator participator = (Participator)getAccount(access_token);
                System.out.println("Found party: " + party);
                System.out.println("Found Participator: " + participator);

                party.addParticipator(participator);
                party = partyRepository.saveAndFlush(party);
                System.out.println("Result party: " + party);
                participator.addParty(party);
                System.out.println("SEMI Result participator: " + participator);
                participator = participatorRepository.saveAndFlush(participator);
                System.out.println("Result participator: " + participator);


                return new ResponseEntity<>("Joined repository " + party.getName(), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Code word mismatch", statusCodeCreator.codeWordMismatch());
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Server error", statusCodeCreator.serverError());
        }

    }

    @GetMapping("/app/participator/my_party_list")
    public ResponseEntity<Set<PartyData>> getPartiesByParticipator(
            @RequestHeader String access_token
    ){
        try{
            Participator participator = (Participator)getAccount(access_token);
            return new ResponseEntity<>(PartyData.createPartyData(participator.getParties()), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }

    }

    @PostMapping("/app/participator/upload_code_words")
    public ResponseEntity<String> uploadCodeWordsForParticipator(
            @RequestHeader String access_token,
            @RequestBody List<String> code_words
            ){
        try{
            Participator participator = (Participator)getAccount(access_token);
            if(participator == null){
                return new ResponseEntity<>("", statusCodeCreator.userNotFound());
            }
            int initCount = participator.getCodeWords().size();
            participator.addCodeWords(code_words);
            int endCount = participator.getCodeWords().size();
            return new ResponseEntity<>("Added " + Integer.toString(endCount - initCount), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return  new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @GetMapping("/app/host/create_qr_token")
    public ResponseEntity<String> create_qr_token(
            @RequestHeader String access_token,
            @RequestParam Integer subject_id
    ){
        try{
            Host host = (Host)getAccount(access_token);
            if(host == null){
                return new ResponseEntity<>("", statusCodeCreator.userNotFound());
            }
            Date now = Date.from(Instant.now());
            Subject subject = subjectRepository.getBySubjectId(subject_id);
            if(subject == null){
                return new ResponseEntity<>("", statusCodeCreator.subjectNotFound());
            }
            String qr_token = qrParser.createQrToken(subject, host, now);
            host.setQrToken(qr_token);
            hostRepository.saveAndFlush(host);
            return new ResponseEntity<>(qr_token, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Server error", statusCodeCreator.serverError());
        }
    }


    @PostMapping("/app/participator/submit_qr_token")
    public ResponseEntity<String> submitQrToken(
            @RequestHeader String access_token,
            @RequestParam String qr_token
            ){
        try{
            Participator participator = (Participator) getAccount(access_token);
            if(participator == null){
                return new ResponseEntity<>("Account not found", statusCodeCreator.userNotFound());
            }
            Host host = hostRepository.getByUuid(qrParser.getHostUUID(qr_token));
            if(host == null){
                return new ResponseEntity<>("Account not found", statusCodeCreator.userNotFound());
            }
            Subject subject = subjectRepository.getBySubjectId(qrParser.getSubjectID(qr_token));
            if(subject == null){
                return new ResponseEntity<>("Subject not found", statusCodeCreator.subjectNotFound());
            }
            Visit visit = new Visit(qrParser.getDate(qr_token), participator, host, subject);
            visitRepository.saveAndFlush(visit);
            return new ResponseEntity<>("Visit scored", HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Server error", statusCodeCreator.serverError());
        }
    }

    @GetMapping("/app/host/subjects_by_date")
    public ResponseEntity<Set<Subject>> getSubjectsByDate(
            @RequestHeader String access_token,
            @RequestParam long timestamp
    ){
        try{
            Host host = (Host)getAccount(access_token);
            if(host == null){
                return new ResponseEntity(null, statusCodeCreator.userNotFound());
            }

            Date date = new Date();
            date.setTime(timestamp);
            Set<Subject> subjects = subjectRepository.getAllByStartDateBeforeAndFinishDateAfter(date, date);
            subjects.removeIf(subject -> !subject.getHosts().contains(host));

            return new ResponseEntity<>(subjects, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @GetMapping("/app/host/find_subject")
    public ResponseEntity<Set<Subject>> getSubjectsList(
            @RequestHeader String access_token,
            @RequestParam String code_word
    ){
        try{
            Set<Subject> subjects = subjectRepository.getAllByBroadcastWord(code_word);
            if(subjects == null){
                return new ResponseEntity<>(new HashSet<>(), HttpStatus.OK);
            }
            return  new ResponseEntity<>(subjects, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return  new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @GetMapping("/app/host/find_subjects_by_code_words")
    public ResponseEntity<Set<Subject>> findSubjectsByCodeWords(
            @RequestHeader String access_token
    ){
        try{
            Host host = (Host)getAccount(access_token);
            if(host == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            Set<Subject> subjects = subjectRepository.getAllByBroadcastWordIn(host.getCodeWordsStrings());
            return new ResponseEntity<>(subjects, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @PostMapping("/app/host/upload_code_words")
    public ResponseEntity<String> uploadCodeWordsForHost(
            @RequestHeader String access_token,
            @RequestBody List<String> code_words
    ){
        try{
            Host host = (Host)getAccount(access_token);
            if(host == null){
                return new ResponseEntity<>("", statusCodeCreator.userNotFound());
            }
            int initCount = host.getCodeWords().size();
            host.addCodeWords(code_words);
            int endCount = host.getCodeWords().size();
            return new ResponseEntity<>("Added " + Integer.toString(endCount - initCount), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return  new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @PostMapping("/app/host/join_subject")
    public ResponseEntity<String> joinSubject(
            @RequestParam Integer subject_id,
            @RequestParam String code_word,
            @RequestHeader String access_token
    ){
        try{
            Subject subject = subjectRepository.getBySubjectId(subject_id);
            if(subject == null){
                return new ResponseEntity<>("Not found", statusCodeCreator.userNotFound());
            }

            if(subject.getBroadcastWord().equals(code_word)){
                Host host = (Host)getAccount(access_token);
                subject.addHost(host);
                subject = subjectRepository.saveAndFlush(subject);

                host.addSubject(subject);
                host = hostRepository.saveAndFlush(host);


                return new ResponseEntity<>("Joined subject " + subject.getName(), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Code word mismatch", statusCodeCreator.codeWordMismatch());
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Server error", statusCodeCreator.serverError());
        }

    }

    @GetMapping("/app/host/my_subjects_list")
    public ResponseEntity<Set<Subject>> getSubjectsByHost(
            @RequestHeader String access_token
    ){
        try{
            Host host = (Host)getAccount(access_token);

            return new ResponseEntity<>(host.getSubjects(), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }
    @GetMapping("/app/host/get_party_by_subject_id")
    public ResponseEntity<Set<PartyData>> getPartiesBySubject(
            @RequestHeader String access_token,
            @RequestParam Integer subject_id
    ){
        try{
            Host host = (Host)getAccount(access_token);
            Subject subject = subjectRepository.getBySubjectId(subject_id);
            if(subject == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            return new ResponseEntity<>(PartyData.createPartyData(subject.getParties()), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @GetMapping("/app/host/get_all_parties")
    public ResponseEntity<Set<PartyData>> getPartiesByHost(
            @RequestHeader String access_token
    ){
        try{
            Host host = (Host)getAccount(access_token);
            HashSet<PartyData> partyData = new HashSet<>();
            host.getSubjects().forEach(subject -> {
                partyData.addAll(PartyData.createPartyData(subject.getParties()));
            });

            return new ResponseEntity<>(partyData, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }



    @GetMapping("/app/host/credentials")
    public ResponseEntity<HashMap> getHostCredentials(
            @RequestHeader String access_token
    ){
        try{
            Host host = (Host)getAccount(access_token);
            if(host == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            HashMap<String, String> data = new HashMap<>();
            data.put("name", host.getName());
            data.put("email", host.getEmail());
            return new ResponseEntity<>(data, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }
    @PostMapping("/app/host/credentials")
    public ResponseEntity<String> postHostCredentials(
            @RequestHeader String access_token,
            @RequestBody HashMap<String,String> data
    ){
        try{
            Host host = (Host)getAccount(access_token);
            if(host == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            String response = "";
            if(data.containsKey("name")){
                host.setName(data.get("name"));
                response = response + "Name set to " + data.get("name") + " ";
            }
            if(data.containsKey("email")){
                host.setEmail(data.get("email"));
                response = response + "Email set to " + data.get("email");
            }
            hostRepository.saveAndFlush(host);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }
    @GetMapping("/app/participator/credentials")
    public ResponseEntity<HashMap> getParticipatorCredentials(
            @RequestHeader String access_token
    ){
        try{
            Participator participator = (Participator) getAccount(access_token);
            if(participator == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            HashMap<String, String> data = new HashMap<>();
            data.put("name", participator.getName());
            data.put("email", participator.getEmail());
            return new ResponseEntity<>(data, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }
    @PostMapping("/app/participator/credentials")
    public ResponseEntity<String> postParticipatorCredentials(
            @RequestHeader String access_token,
            @RequestBody HashMap<String,String> data
    ){
        try{
            Participator participator = (Participator) getAccount(access_token);
            if(participator == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            String response = "";
            if(data.containsKey("name")){
                participator.setName(data.get("name"));
                response = response + "Name set to " + data.get("name") + " ";
            }
            if(data.containsKey("email")){
                participator.setEmail(data.get("email"));
                response = response + "Email set to " + data.get("email");
            }
            participatorRepository.saveAndFlush(participator);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @PostMapping("/app/logout")
    public ResponseEntity<String> logout(@RequestHeader String refresh_token){
        try{
            if(tokenParser.getAccountType(refresh_token) == TokenParser.ACCOUNT.ACCOUNT_HOST){
                Host host = (Host)getAccount(refresh_token);
                TokenData t = tokenParser.createTokenData(host.getUuid(), host.getPassword(), TokenParser.ACCOUNT.ACCOUNT_HOST,Date.from(Instant.now()));
                host.setAccessToken(t.getAccess_token());
                host.setRefreshToken(t.getRefresh_token());
                hostRepository.saveAndFlush(host);
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }else if(tokenParser.getAccountType(refresh_token) == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
                Participator p = (Participator)getAccount(refresh_token);
                TokenData t = tokenParser.createTokenData(p.getUuid(), p.getPassword(), TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR,Date.from(Instant.now()));
                p.setAccessToken(t.getAccess_token());
                p.setRefreshToken(t.getRefresh_token());
                participatorRepository.saveAndFlush(p);
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Some thing went wrong", statusCodeCreator.tokenNotValid());
            }

        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Some thing went wrong", statusCodeCreator.serverError());
        }
    }



    private Account getAccount(String access_token){
        try{
            TokenParser.ACCOUNT account = tokenParser.getAccountType(access_token);
            String UUID = tokenParser.getUUID(access_token);
            String password = tokenParser.getPassword(access_token);
            if(account == TokenParser.ACCOUNT.ACCOUNT_MANAGER){
                return managerRepository.findByUuidAndPassword(UUID, password);
            }else if(account == TokenParser.ACCOUNT.ACCOUNT_HOST){
                return hostRepository.findByUuidAndPassword(UUID, password);
            }else if(account == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
                return participatorRepository.findByUuidAndPassword(UUID, password);
            }else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private Account getAccount(String UUID, String password, TokenParser.ACCOUNT type){
        if(type == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
            Participator participator = participatorRepository.findByUuidAndPassword(UUID, password);
            return participator;
        }else if(type == TokenParser.ACCOUNT.ACCOUNT_HOST){
            Host host = hostRepository.findByUuidAndPassword(UUID,password);
            return host;
        }else{
            return null;
        }
    }

    private ResponseEntity<TokenData> error(HttpStatus status){
        return new ResponseEntity<TokenData>(new TokenData(), status);
    }

}
