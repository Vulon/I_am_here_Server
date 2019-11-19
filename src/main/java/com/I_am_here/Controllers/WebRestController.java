package com.I_am_here.Controllers;

import com.I_am_here.Database.Entity.*;
import com.I_am_here.Database.Repository.*;
import com.I_am_here.Security.TokenParser;
import com.I_am_here.Services.StatusCodeCreator;
import com.I_am_here.TransportableData.ExtendedPartyData;
import com.I_am_here.TransportableData.SubjectData;
import com.I_am_here.TransportableData.TokenData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


/**
 * ServerController. Methods of this class get called when server gets requests for urls like /web/*
 * Most of this methods can be called only if a request has a valid access token.
 * Access token has a field describing, what type of Account this token is associated with, so if a user is
 * registered as Host or Participator he can access /web/* routes only if he is also registered as Manager
 */
@RestController
public class WebRestController {

    private ManagerRepository managerRepository;
    private PartyRepository partyRepository;
    private SubjectRepository subjectRepository;
    private HostRepository hostRepository;
    private TokenParser tokenParser;
    private StatusCodeCreator statusCodeCreator;
    private ParticipatorRepository participatorRepository;


    public WebRestController(ManagerRepository managerRepository, PartyRepository partyRepository, SubjectRepository subjectRepository, HostRepository hostRepository, TokenParser tokenParser, StatusCodeCreator statusCodeCreator, ParticipatorRepository participatorRepository) {
        this.managerRepository = managerRepository;
        this.partyRepository = partyRepository;
        this.subjectRepository = subjectRepository;
        this.hostRepository = hostRepository;
        this.tokenParser = tokenParser;
        this.statusCodeCreator = statusCodeCreator;
        this.participatorRepository = participatorRepository;
    }

    /**
     * This method is used to get new tokens. If everything is fine it should return response with new TokenData
     * and status code 200. If server can't find such Manager in Database, it returns status code 409
     */
    @PostMapping("/web/login")
    @ResponseBody
    public ResponseEntity<TokenData> login(@RequestHeader String UUID, @RequestHeader String password){
        try{
            Manager manager = managerRepository.findByUuidAndPassword(UUID, password);
            if(manager == null){
                return new ResponseEntity<>(new TokenData(), statusCodeCreator.userNotFound());
            }
            TokenData data = tokenParser.createTokenData(manager.getUuid(), password, TokenParser.ACCOUNT.ACCOUNT_MANAGER, Date.from(Instant.now()));

            manager.setAccessToken(data.getAccess_token());
            manager.setRefreshToken(data.getRefresh_token());
            managerRepository.saveAndFlush(manager);
            System.out.println("Entered manager: " + manager);

            return new ResponseEntity<TokenData>(data, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }


    }


    /**
     * This method is used to create new account. It will create Manager account and store data in manager table in  database.
     * Method returns TokenData with new generated tokens and status code 200. If the database contains such user already,
     * server returns status code 409
     * @param name Not necessary
     * @param email Not necessary
     */
    @PostMapping("/web/register")
    public ResponseEntity<TokenData> register(
            @RequestHeader String UUID,
            @RequestHeader String password,
            @RequestParam(name = "name", defaultValue = "", required = false) String name,
            @RequestParam(name = "email", defaultValue = "", required = false) String email){
        try{

            Manager manager = managerRepository.getByUuid(UUID);
            if(manager != null){
                return new ResponseEntity<>(new TokenData(), statusCodeCreator.alreadyRegistered());
            }
            Date now = Date.from(Instant.now());
            TokenData data = tokenParser.createTokenData(UUID, password, TokenParser.ACCOUNT.ACCOUNT_MANAGER, now);
            manager = new Manager(UUID, name, email, password, data.getAccess_token(), data.getRefresh_token(),
                    new HashSet<Subject>(), new HashSet<Host>(), new HashSet<Party>());
            System.out.println("Saving manager + " + manager.toString());
            managerRepository.saveAndFlush(manager);
            return new ResponseEntity<TokenData>(data, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @GetMapping("/web/credentials")
    public ResponseEntity<HashMap> getManagerCredentials(
            @RequestHeader String access_token
    ){
        try{
            Manager manager = getManagerAccount(access_token);
            if(manager == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            HashMap<String, String> data = new HashMap<>();
            data.put("name", manager.getName());
            data.put("email", manager.getEmail());
            return new ResponseEntity<>(data, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }
    @PostMapping("/web/credentials")
    public ResponseEntity<String> postManagerCredentials(
            @RequestHeader String access_token,
            @RequestBody HashMap<String,String> data
    ){
        try{
            Manager manager = getManagerAccount(access_token);
            if(manager == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            String response = "";
            if(data.containsKey("name")){
                manager.setName(data.get("name"));
                response = response + "Name set to " + data.get("name") + " ";
            }
            if(data.containsKey("email")){
                manager.setEmail(data.get("email"));
                response = response + "Email set to " + data.get("email");
            }
            managerRepository.saveAndFlush(manager);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }


    /**
     * Create new Party for that manager. If an error occurs, 418 status is returned.
     * Name of a party should be unique, so if such manager already has a party with that name, server will return 409 error.
     * @param access_token - manager access token
     * @param name - Name of a new party
     * @param broadcast_word - Code word that will be used to enter this party
     * @param description - Optional party description
     */
    @PostMapping("/web/create_party")
    public ResponseEntity<String> createParty(
            @RequestHeader String access_token,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "code") String broadcast_word,
            @RequestParam(name = "description", required = false, defaultValue = "Some event") String description

    ){
        try{
            String UUID = tokenParser.getUUID(access_token);
            Manager manager = managerRepository.getByUuid(UUID);
            if(manager == null){
                return new ResponseEntity<>("Incorrect token", statusCodeCreator.userNotFound());
            }
            Party party = partyRepository.getByNameAndManager(name, manager);
            if(party != null){
                return new ResponseEntity<>("Name of a party should be unique for a single user", statusCodeCreator.notUniqueName());
            }
            Party newParty = new Party(name, description, broadcast_word, manager);
            System.out.println("newParty: " + newParty);
            newParty = partyRepository.saveAndFlush(newParty);
            System.out.println("New Party afted saving: " + newParty);
            manager.addParty(newParty);
            managerRepository.saveAndFlush(manager);
            return new ResponseEntity<>("Created " + name, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Request handle error", statusCodeCreator.serverError());
        }
    }


    @GetMapping("/web/parties")
    public ResponseEntity<ArrayList<ExtendedPartyData>> getPartyData(
            @RequestHeader String access_token
    ){
        try{
            String UUID = tokenParser.getUUID(access_token);
            Manager manager = managerRepository.getByUuid(UUID);
            if(manager == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            ArrayList<ExtendedPartyData> list = new ArrayList<>();
            partyRepository.getAllByManager(manager).forEach(party -> {
                list.add(new ExtendedPartyData(party));
            });

            return new ResponseEntity<>(list, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }

    @PostMapping("/web/party")
    public ResponseEntity<String> postOrUpdateParty(
            @RequestHeader String access_token,
            @RequestBody ExtendedPartyData party_data
    ){
        try{
            Manager manager = getManagerAccount(access_token);
            if(manager == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            System.out.println("Got the party data:" + party_data.toString());
            Party party = partyRepository.getByParty(party_data.getId());
            String response;
            if(party == null){
                response = "Created new party:   ";
                party = new Party(party_data.getName(), party_data.getDescription(), party_data.getCode(), manager);

            }else{
                response = "Updated party:   ";
                party.setName(party_data.getName());
                party.setDescription(party_data.getDescription());
                party.setBroadcastWord(party_data.getCode());
                //TODO FIXED IT?
            }
            final Party partyRef = party;
            Set<Subject> subjectSet = new HashSet<>();
            party_data.getSubjects().forEach(stringStringHashMap -> subjectSet.add(subjectRepository.getBySubjectId(Integer.parseInt(stringStringHashMap.get("id")))));
            subjectSet.forEach(subject -> {
                if(!partyRef.getSubjects().contains(subject)){
                    partyRef.addSubject(subject);
                    subjectRepository.save(subject);
                }
            });
            partyRef.getSubjects().forEach(subject -> {
                if(!subjectSet.contains(subject)){
                    partyRef.removeSubject(subject);
                    subjectRepository.save(subject);
                }
            });
            subjectRepository.flush();

            Set<Participator> participatorSet = new HashSet<>();
            party_data.getParticipators().forEach(stringStringHashMap -> participatorSet.add(participatorRepository.getByParticipatorId(Integer.parseInt(stringStringHashMap.get("id")))));
            participatorSet.forEach(participator -> {
                if(!partyRef.getParticipators().contains(participator)){
                    partyRef.addParticipator(participator);
                    participatorRepository.save(participator);
                }
            });
            partyRef.getParticipators().forEach(participator -> {
                if(!participatorSet.contains(participator)){
                    partyRef.removeParticipator(participator);
                    participatorRepository.save(participator);
                }
            });
            participatorRepository.flush();

            partyRepository.saveAndFlush(partyRef);

            return new ResponseEntity<>(response + party.toString(), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }




    @GetMapping("/web/subjects")
    public ResponseEntity<Set<SubjectData>> getSubjects(
            @RequestHeader String access_token
    ){
        try{
            String UUID = tokenParser.getUUID(access_token);
            Manager manager = managerRepository.getByUuid(UUID);
            if(manager == null){
                return new ResponseEntity<>(null, statusCodeCreator.userNotFound());
            }
            HashSet<SubjectData> subjectData = new HashSet<>();
            manager.getSubjects().forEach(subject -> {
                subjectData.add(new SubjectData(subject));
            });
            return new ResponseEntity<>(subjectData, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, statusCodeCreator.serverError());
        }
    }


    @GetMapping("/web/refresh")
    public ResponseEntity<TokenData> updateAccessToken(
            @RequestHeader String refresh_token
    ){
        try{
            String UUID = tokenParser.getUUID(refresh_token);
            String password = tokenParser.getPassword(refresh_token);
            TokenParser.ACCOUNT account_type = tokenParser.getAccountType(refresh_token);

            String access_token = tokenParser.createToken(UUID, password, TokenParser.TYPE.ACCESS, Date.from(Instant.now()), account_type);
            if (account_type == TokenParser.ACCOUNT.ACCOUNT_MANAGER) {
                Manager manager = managerRepository.getByUuidAndPassword(UUID, password);
                if (manager == null) {
                    return error(statusCodeCreator.userNotFound());
                }
                manager.setAccessToken(access_token);
                managerRepository.saveAndFlush(manager);

                return new ResponseEntity<>(tokenParser.getTokenData(manager), HttpStatus.OK);
            }else{
                return error(statusCodeCreator.missingAccountTypeField());
            }
        }catch (Exception e){
            e.printStackTrace();
            return error(statusCodeCreator.serverError());
        }
    }


    @PostMapping("/web/create_subject")
    public ResponseEntity<String> postSubject(
            @RequestHeader String access_token,
            @RequestBody SubjectData subjectData
            ){
        try{
            Manager manager = managerRepository.findByUuid(tokenParser.getUUID(access_token));
            if(manager == null){
                return new ResponseEntity<>("Not found", statusCodeCreator.userNotFound());
            }
            Subject subject = subjectRepository.getBySubjectId(subjectData.getId());
            HashSet<Party> parties = new HashSet<>();
            subjectData.getParties().forEach(stringStringHashMap -> {
                parties.add(partyRepository.getByParty(Integer.parseInt(stringStringHashMap.get("id"))));
            });
            HashSet<Host> hosts = new HashSet<>();
            subjectData.getHosts().forEach(stringStringHashMap -> {
                hosts.add(hostRepository.getByHostId(Integer.parseInt(stringStringHashMap.get("id"))));
            });

            if(subject == null){
                subject = new Subject();
                subject.setManager(manager);
                subject.setBroadcastStart(new Date());
                subject.setData(subjectData);

            }else{
                subject.setData(subjectData);
            }
            final Subject subjectRef = subject;

            parties.forEach(party -> {
                if(!subjectRef.getParties().contains(party)){
                    subjectRef.addParty(party);
                    partyRepository.save(party);
                }
            });
            subjectRef.getParties().forEach(party -> {
                if(!parties.contains(party)){
                    subjectRef.removeParty(party);
                    partyRepository.save(party);
                }
            });
            partyRepository.flush();

            hosts.forEach(host -> {
                if(!subjectRef.getHosts().contains(host)){
                    subjectRef.addHost(host);
                    hostRepository.save(host);
                }
            });
            subjectRef.getHosts().forEach(host -> {
                if(!hosts.contains(host)){
                    subjectRef.removeHost(host);
                    hostRepository.save(host);
                }
            });
            hostRepository.flush();


            subjectRepository.saveAndFlush(subject);
            return new ResponseEntity<>("Subject created " + subject.toString() , HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Server error", statusCodeCreator.serverError());
        }
    }



    private Manager getManagerAccount(String access_token){
        String uuid = tokenParser.getUUID(access_token);
        Manager manager = managerRepository.getByUuid(uuid);
        return  manager;
    }


    /**
     * Not sure, that this method is required, it clears security context, so server will not have any cookies
     * after this method invokes
     */
    @PostMapping("/web/logout")
    @ResponseBody
    public ResponseEntity<String> logout(){
        SecurityContextHolder.clearContext();
        return new ResponseEntity<String>("Logged out", HttpStatus.OK);
    }
    private ResponseEntity<TokenData> error(HttpStatus status){
        return new ResponseEntity<TokenData>(new TokenData(), status);
    }
}
