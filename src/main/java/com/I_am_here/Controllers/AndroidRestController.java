package com.I_am_here.Controllers;

import com.I_am_here.Database.Account;
import com.I_am_here.Database.Entity.*;
import com.I_am_here.Database.Repository.*;
import com.I_am_here.Security.TokenParser;
import com.I_am_here.Services.QRParser;
import com.I_am_here.Services.SecretDataLoader;
import com.I_am_here.Services.StatusCodeCreator;
import com.I_am_here.TransportableData.*;
import com.google.api.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


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
    private Participator_Code_wordRepository participator_code_wordRepository;
    private Host_Code_wordRepository host_code_wordRepository;

    private ManagerRepository managerRepository;
    private TokenParser tokenParser;
    private SecretDataLoader secretDataLoader;
    private StatusCodeCreator statusCodeCreator;
    private QRParser qrParser;


    public AndroidRestController(HostRepository hostRepository, ParticipatorRepository participatorRepository, PartyRepository partyRepository, SubjectRepository subjectRepository, VisitRepository visitRepository, Participator_Code_wordRepository participator_code_wordRepository, Host_Code_wordRepository host_code_wordRepository, ManagerRepository managerRepository, TokenParser tokenParser, SecretDataLoader secretDataLoader, StatusCodeCreator statusCodeCreator, QRParser qrParser) {
        this.hostRepository = hostRepository;
        this.participatorRepository = participatorRepository;
        this.partyRepository = partyRepository;
        this.subjectRepository = subjectRepository;
        this.visitRepository = visitRepository;
        this.participator_code_wordRepository = participator_code_wordRepository;
        this.host_code_wordRepository = host_code_wordRepository;
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
            @RequestParam(name = "email_secure", defaultValue = "true", required = false) String email_secure,
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
                host.setEmail_secured(email_secure.equals("true"));
                hostRepository.saveAndFlush(host);
                return new ResponseEntity<>(data, HttpStatus.OK);
            }else if(type == TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR){
                TokenData data = tokenParser.createTokenData(UUID, password, TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR, now);
                Participator participator = new Participator(UUID, name, email, password, data);
                participator.setEmail_secured(email_secure.equals("true"));
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

    @PostMapping("/app/participator/code_words")
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
            participator.addCodeWords(code_words, participator_code_wordRepository);
            participator_code_wordRepository.flush();
            participatorRepository.saveAndFlush(participator);
            int endCount = participator.getCodeWords().size();
            return new ResponseEntity<>("Added " + Integer.toString(endCount - initCount), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return  new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }


    @GetMapping("/app/participator/code_words")
    public ResponseEntity<Set<String>> getParticipatorCodeWords(
            @RequestHeader String access_token
    ){
        try{
            Participator participator = (Participator)getAccount(access_token);
            if(participator == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            return new ResponseEntity<>(participator.getCodeWordsStrings(), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return  new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }
    @DeleteMapping("/app/participator/code_words")
    public ResponseEntity<String> deleteParticipatorCodeWords(
            @RequestHeader String access_token,
            @RequestBody Set<String> code_words
    ){
        try{
            Participator participator = (Participator)getAccount(access_token);
            if(participator == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            int init_size = participator.getCodeWords().size();

            participator_code_wordRepository.deleteAllByCodeWordInAndParticipator(code_words, participator);
            participator_code_wordRepository.flush();

            participator.removeCodeWords(code_words);
            int end_size = participator.getCodeWords().size();
            participatorRepository.saveAndFlush(participator);
            return new ResponseEntity<>("Removed " + (end_size - init_size) + " words", HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return  new ResponseEntity<>(null, statusCodeCreator.serverError());
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
            if(data.containsKey("email_secured")){
                participator.setEmail_secured(data.get("email_secured").equals("true"));
                response = response + "Email Secured: set to " + participator.isEmailSecured();
            }
            participatorRepository.saveAndFlush(participator);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
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
            Host host = hostRepository.getByHostId(qrParser.getHostID(qr_token));
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

    @GetMapping("/app/participator/get_user_by_id")
    public ResponseEntity<HashMap> getAccountNameForParticipator(
            @RequestHeader String access_token,
            @RequestParam Integer account_id,
            @RequestParam(name = "account_type") String account_type
    ){
        try{
            Participator participator = (Participator) getAccount(access_token);
            if(participator == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            HashMap<String, String> answer = new HashMap<>();
            if(account_type.equals(TokenParser.ACCOUNT.ACCOUNT_HOST.name())){
                Host target = hostRepository.getByHostId(account_id);
                if(target == null){
                    answer.put("error", "Host not found for that id");
                    return new ResponseEntity<>(answer, HttpStatus.OK);
                }
                answer.put("name", target.getName());
                if(!target.isEmailSecured()){
                    answer.put("email", target.getEmail());
                }
                return new ResponseEntity<>(answer, HttpStatus.OK);
            }else if(account_type.equals(TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR.name())){
                Participator target = participatorRepository.getByParticipatorId(account_id);
                if(target == null){
                    answer.put("error", "Participator not found for that id");
                    return new ResponseEntity<>(answer, HttpStatus.OK);
                }
                answer.put("name", target.getName());
                if(!target.isEmailSecured()){
                    answer.put("email", target.getEmail());
                }
                return new ResponseEntity<>(answer, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(null, statusCodeCreator.invalidParameter());
            }

        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null,  statusCodeCreator.serverError());
        }
    }

    @PostMapping("/app/participator/leave_party")
    public ResponseEntity<String> leaveParty(
            @RequestHeader String access_token,
            @RequestParam Integer party_id
    ){
        try{
            Participator participator = (Participator) getAccount(access_token);
            if(participator == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            Party party = partyRepository.getByParty(party_id);
            if(party == null){
                return new ResponseEntity<>("Party not found", statusCodeCreator.partyNotFound());
            }
            participator.removeParty(party);
            participatorRepository.saveAndFlush(participator);
            return new ResponseEntity<>("Removed party " + party.getName(), HttpStatus.OK);

        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Server error",  statusCodeCreator.serverError());
        }
    }

    @GetMapping("/app/participator/get_party_by_id")
    public ResponseEntity<Set<HashMap<String,String>>> getPartyMembersForParticipator(
            @RequestHeader String access_token,
            @RequestParam Integer party_id
    ){
        try{
            Participator participator = (Participator) getAccount(access_token);
            if(participator == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            Party party = partyRepository.getByParty(party_id);
            if(party == null){
                return new ResponseEntity<>(null, statusCodeCreator.entityNotFound());
            }
            if(participator.getParties().contains(party)){
                HashSet<HashMap<String,String>> set = new HashSet<HashMap<String,String>>();
                party.getParticipators().forEach(member -> {
                    if (!member.getParticipatorId().equals(participator.getParticipatorId())){
                        HashMap<String, String> map = new HashMap<>();
                        map.put("id", member.getParticipatorId().toString());
                        map.put("name", member.getName());
                        set.add(map);
                    }
                });
                return new ResponseEntity<>(set, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(null, statusCodeCreator.entityNotFound());
            }

        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null,  statusCodeCreator.serverError());
        }
    }

    @GetMapping("/app/participator/subjects_by_date")
    public ResponseEntity<Set<SubjectData>> getSubjectsByDateForParticipator(
            @RequestHeader String access_token,
            @RequestParam long timestamp
    ){
        try{
            Participator participator = (Participator) getAccount(access_token);
            if(participator == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }

            Date date = new Date();
            final HashSet<SubjectData> subjectData = new HashSet<>();
            date.setTime(timestamp);
            Set<Subject> subjects = subjectRepository.getAllByStartDateBeforeAndFinishDateAfter(date, date);
            subjects.stream().filter(subject ->
                subject.getParties().stream().anyMatch(party -> party.getParticipators().contains(participator))
            ).forEach(subject -> subjectData.add(new SubjectData(subject)));


            return new ResponseEntity<>(subjectData, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @GetMapping("/app/participator/visits_by_subject_id")
    public ResponseEntity<Set<VisitData>> getSubjectsVisits(
            @RequestHeader String access_token,
            @RequestParam Integer subject_id
    ){
        try{
            Participator participator = (Participator) getAccount(access_token);
            if(participator == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            Subject subject = subjectRepository.getBySubjectId(subject_id);
            if(subject == null){
                return new ResponseEntity<>(null, statusCodeCreator.entityNotFound());
            }
            HashSet<VisitData> set = new HashSet<>();

            participator.getVisits().stream().filter(visit -> visit.getSubject().equals(subject)).forEach(visit -> set.add(new VisitData(visit)));
            return new ResponseEntity<>(set, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
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


    @GetMapping("/app/host/subjects_by_date")
    public ResponseEntity<Set<SubjectData>> getSubjectsByDate(
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
            HashSet<SubjectData> subjectData = new HashSet<>();
            subjects.forEach(subject -> {
                subjectData.add(new SubjectData(subject));
            });
            return new ResponseEntity<>(subjectData, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @GetMapping("/app/host/find_subject")
    public ResponseEntity<Set<SubjectData>> getSubjectsList(
            @RequestHeader String access_token,
            @RequestParam String code_word
    ){
        try{
            Set<Subject> subjects = subjectRepository.getAllByBroadcastWord(code_word);
            if(subjects == null){
                return new ResponseEntity<>(new HashSet<>(), HttpStatus.OK);
            }
            HashSet<SubjectData> subjectData = new HashSet<>();
            subjects.forEach(subject -> {
                subjectData.add(new SubjectData(subject));
            });
            return  new ResponseEntity<>(subjectData, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return  new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @GetMapping("/app/host/find_subjects_by_code_words")
    public ResponseEntity<Set<SubjectData>> findSubjectsByCodeWords(
            @RequestHeader String access_token
    ){
        try{
            Host host = (Host)getAccount(access_token);
            if(host == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            HashSet<SubjectData> subjectData = new HashSet<>();
            Set<Subject> subjectSet = subjectRepository.getAllByBroadcastWordIn(host.getCodeWordsStrings());

            subjectSet.forEach(subject -> {
                subjectData.add(new SubjectData(subject));
            });
            return new ResponseEntity<>(subjectData, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @PostMapping("/app/host/code_words")
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
            host.addCodeWords(code_words, host_code_wordRepository);
            host_code_wordRepository.flush();
            int endCount = host.getCodeWords().size();
            hostRepository.saveAndFlush(host);
            return new ResponseEntity<>("Added " + Integer.toString(endCount - initCount), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return  new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @GetMapping("/app/host/code_words")
    public ResponseEntity<Set<String>> getHostCodeWords(
            @RequestHeader String access_token
    ){
        try{
            Host host = (Host)getAccount(access_token);
            if(host == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            return new ResponseEntity<>(host.getCodeWordsStrings(), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return  new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }
    @DeleteMapping("/app/host/code_words")
    public ResponseEntity<String> deleteHostCodeWords(
            @RequestHeader String access_token,
            @RequestBody Set<String> code_words
    ){
        try{
            final Host host = (Host)getAccount(access_token);
            if(host == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }

            int init_size = host.getCodeWords().size();

            int count = host_code_wordRepository.deleteAllByCodeWordInAndHost(code_words, host);


            host.removeCodeWords(code_words);
            host_code_wordRepository.flush();
            hostRepository.saveAndFlush(host);

            int end_size = host.getCodeWords().size();
            return new ResponseEntity<>("Removed " + (end_size - init_size) + " words. From repo deleted words: " + count, HttpStatus.OK);
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
    public ResponseEntity<Set<SubjectData>> getSubjectsByHost(
            @RequestHeader String access_token
    ){
        try{
            Host host = (Host)getAccount(access_token);
            HashSet<SubjectData> subjectData = new HashSet<>();
            host.getSubjects().forEach(subject -> {
                subjectData.add(new SubjectData(subject));
            });
            return new ResponseEntity<>(subjectData, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }
    @GetMapping("/app/host/get_party_by_subject_id")
    public ResponseEntity<Set<ExtendedPartyData>> getPartiesBySubject(
            @RequestHeader String access_token,
            @RequestParam Integer subject_id
    ){
        try{
            Host host = (Host)getAccount(access_token);
            Subject subject = subjectRepository.getBySubjectId(subject_id);
            if(subject == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            if (host.getSubjects().contains(subject)){
                return new ResponseEntity<>(ExtendedPartyData.createExtendedPartyData(subject.getParties()),HttpStatus.OK);
            }else{
                return new ResponseEntity<>(null, statusCodeCreator.notAuthorized());
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @GetMapping("/app/host/subject_by_id")
    public ResponseEntity<SubjectData> findSubjectById(
            @RequestHeader String access_token,
            @RequestParam Integer subject_id
    ){
        try{
            Host host = (Host)getAccount(access_token);
            if(host == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }

            Subject subject = subjectRepository.getBySubjectId(subject_id);
            if (subject==null){
                return new ResponseEntity<>(null, statusCodeCreator.subjectNotFound());
            }

            /* Prevent querying of subjects that host has no access to (in case of http request abuse)*/
            if (!host.getSubjects().contains(subject)) {
                return new ResponseEntity<>(null, statusCodeCreator.notAuthorized());
            }
            SubjectData subjectData = new SubjectData(subject);


            return new ResponseEntity<>(subjectData, HttpStatus.OK);
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

    @GetMapping("/app/host/party_by_id")
    public ResponseEntity<ExtendedPartyData> findPartyById(
            @RequestHeader String access_token,
            @RequestParam Integer party_id
    ){
        try{
            Host host = (Host)getAccount(access_token);
            if(host == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }

            Party party = partyRepository.getByParty(party_id);
            if (party==null){
                return new ResponseEntity<>(null, statusCodeCreator.partyNotFound());
            }


            long count = host.getSubjects().stream().filter(subject -> subject.getParties().contains(party)).count();
            if(count < 1){
                return new ResponseEntity<>(null, statusCodeCreator.notAuthorized());
            }else{return new ResponseEntity<>(new ExtendedPartyData(party), HttpStatus.OK);
            }
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
            if(data.containsKey("email_secured")){
                host.setEmail_secured(data.get("email_secured").equals("true"));
                response = response + "Email Secured: set to " + host.isEmailSecured();
            }
            hostRepository.saveAndFlush(host);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }


    @PostMapping("/app/host/list_of_visit_times")
    public ResponseEntity<Set<PartyParticipatorVisits>> getParticipatorVisitTimesForHost(
            @RequestHeader String access_token,
            Integer subject_id,
            Long timestamp,
            @RequestBody Set<Integer> partySet
    ){
        try{
            Host host = (Host)getAccount(access_token);
            if(host == null){
                return  new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            final Date date = new Date(timestamp);

            final Subject subject = subjectRepository.getBySubjectId(subject_id);

            final Set<Visit> visitSet = visitRepository.getAllByHostAndAndSubject(host, subject);
            if(visitSet == null){
                return new ResponseEntity<>(new HashSet<>(), HttpStatus.OK);
            }

            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 1);

            HashSet<PartyParticipatorVisits> partiesData = new HashSet<>();
            partySet.forEach(integer -> partiesData.add(new PartyParticipatorVisits(partyRepository.getByParty(integer))));

            visitSet.stream().filter(visit -> visit.getDate().before(date) && visit.getDate().after(calendar.getTime()))
                    .filter(visit -> {
                        for (PartyParticipatorVisits p : partiesData) {
                            for (Party pp : visit.getParticipator().getParties()) {
                                if (pp.getParty().equals(p.getId())) return true;
                             }
                        }
                        return false;
                    }).forEach(visit -> {
                        for(PartyParticipatorVisits ppvt : partiesData){
                            for (Party p : visit.getParticipator().getParties()) {
                                if (p.getParty().equals(ppvt.getId())) {
                                    ParticipatorVisitTimes pvt;

                                    if ((pvt = ppvt.findParticipator(visit.getParticipator().getParticipatorId()))!=null) {
                                        pvt.addVisit(visit.getDate());
                                        break;
                                    }

                                    pvt = new ParticipatorVisitTimes(visit.getParticipator());
                                    pvt.addVisit(visit.getDate());
                                    ppvt.addParticipatorVisitTime(pvt);
                                    break;
                                }
                            }
                        }
            });

            return new ResponseEntity<>(partiesData.stream().filter(ppv -> !ppv.getParticipator_visit_times().isEmpty()).collect(Collectors.toSet()),
                    HttpStatus.OK);

        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @GetMapping("/app/host/get_user_by_id")
    public ResponseEntity<HashMap> getAccountNameForHost(
            @RequestHeader String access_token,
            @RequestParam Integer account_id,
            @RequestParam(name = "account_type") String account_type
    ){
        try{
            Host host = (Host) getAccount(access_token);
            if(host == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            HashMap<String, String> answer = new HashMap<>();
            if(account_type.equals(TokenParser.ACCOUNT.ACCOUNT_HOST.name())){
                Host target = hostRepository.getByHostId(account_id);
                if(target == null){
                    answer.put("error", "Host not found for that id");
                    return new ResponseEntity<>(answer, HttpStatus.OK);
                }
                answer.put("name", target.getName());
                if(!target.isEmailSecured()){
                    answer.put("email", target.getEmail());
                }
                return new ResponseEntity<>(answer, HttpStatus.OK);
            }else if(account_type.equals(TokenParser.ACCOUNT.ACCOUNT_PARTICIPATOR.name())){
                Participator target = participatorRepository.getByParticipatorId(account_id);
                if(target == null){
                    answer.put("error", "Participator not found for that id");
                    return new ResponseEntity<>(answer, HttpStatus.OK);
                }
                answer.put("name", target.getName());
                if(!target.isEmailSecured()){
                    answer.put("email", target.getEmail());
                }
                return new ResponseEntity<>(answer, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(null, statusCodeCreator.invalidParameter());
            }

        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null,  statusCodeCreator.serverError());
        }
    }

        @PostMapping("/app/host/leave_subject")
    public ResponseEntity<String> leaveSubject(
            @RequestHeader String access_token,
            @RequestParam Integer subject_id
    ){
        try{
            Host host = (Host) getAccount(access_token);
            if(host == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            Subject subject = subjectRepository.getBySubjectId(subject_id);
            if(subject == null){
                return new ResponseEntity<>("Subject not found", statusCodeCreator.partyNotFound());
            }
            host.removeSubject(subject);
            hostRepository.saveAndFlush(host);
            return new ResponseEntity<>("Removed subject " + subject.getName(), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Server error",  statusCodeCreator.serverError());
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
