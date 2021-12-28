package backend.hobbiebackend.service.impl;


import backend.hobbiebackend.handler.NotFoundException;
import backend.hobbiebackend.model.dto.AppClientSignUpDto;
import backend.hobbiebackend.model.dto.BusinessRegisterDto;
import backend.hobbiebackend.model.entities.*;
import backend.hobbiebackend.model.entities.enums.GenderEnum;
import backend.hobbiebackend.model.entities.enums.UserRoleEnum;
import backend.hobbiebackend.model.repostiory.AppClientRepository;
import backend.hobbiebackend.model.repostiory.BusinessOwnerRepository;
import backend.hobbiebackend.model.repostiory.UserRepository;
import backend.hobbiebackend.service.UserRoleService;
import backend.hobbiebackend.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
    private final AppClientRepository appClientRepository;
    private final BusinessOwnerRepository businessOwnerRepository;
    private final UserRoleService userRoleService;


    @Autowired
    public UserServiceImpl(ModelMapper modelMapper, UserRepository userRepository,
                            AppClientRepository appClientRepository,
                           BusinessOwnerRepository businessOwnerRepository, UserRoleService userRoleService) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
        this.appClientRepository = appClientRepository;
        this.businessOwnerRepository = businessOwnerRepository;
        this.userRoleService = userRoleService;

    }

    @Override
    public List<UserEntity> seedUsersAndUserRoles() {
        List<UserEntity> seededUsers = new ArrayList<>();

        //simple user
        if(appClientRepository.count() == 0) {
            UserRoleEntity userRoleEntity = new UserRoleEntity();
            userRoleEntity.setRole(UserRoleEnum.USER);
            UserRoleEntity userRole = this.userRoleService.saveRole(userRoleEntity);
            UserRoleEntity userRoleEntity2 = new UserRoleEntity();
            userRoleEntity2.setRole(UserRoleEnum.ADMIN);
            UserRoleEntity adminRole = this.userRoleService.saveRole(userRoleEntity2);
            AppClient user = new AppClient();
            user.setUsername("user");
            user.setEmail("n13@gmail.com");
//            user.setPassword(this.passwordEncoder.encode("topsecret"));
            user.setPassword("123");
            user.setRoles(List.of(userRole));
            user.setFullName("Nikoleta Doykova");
            user.setGender(GenderEnum.FEMALE);


            //admin
            AppClient admin = new AppClient();
            admin.setUsername("admin");
            admin.setEmail("n11@gamil.com");
//            admin.setPassword(this.passwordEncoder.encode("topsecret"));
            admin.setPassword("123");
            admin.setRoles(List.of(adminRole, userRole));
            admin.setFullName("Full name of admin here");
            admin.setGender(GenderEnum.FEMALE);
            appClientRepository.save(user);
            appClientRepository.save(admin);
            seededUsers.add( user);
            seededUsers.add( admin);

        }
        if(businessOwnerRepository.count() == 0){

            UserRoleEntity userRoleEntity3 = new UserRoleEntity();
            userRoleEntity3.setRole(UserRoleEnum.BUSINESS_USER);
            UserRoleEntity businessRole = this.userRoleService.saveRole(userRoleEntity3);
            //business_user
            BusinessOwner business_user = new BusinessOwner();
            business_user.setUsername("business");
            business_user.setEmail("n10@gamil.com");
//            business_user.setPassword(this.passwordEncoder.encode("topsecret"));
            business_user.setPassword("123");
            business_user.setRoles(List.of(businessRole));
            business_user.setBusinessName("My Business name");
            business_user.setAddress("My business address");
            businessOwnerRepository.save(business_user);
            seededUsers.add( business_user);
        }
        return seededUsers;
    }

    @Override
    public AppClient register(AppClientSignUpDto user) {

            UserRoleEntity userRole = this.userRoleService.getUserRoleByEnumName(UserRoleEnum.USER);
            AppClient appClient = this.modelMapper.map(user, AppClient.class);
            appClient.setRoles(List.of(userRole));
    //            appClient.setPassword(this.passwordEncoder.encode(user.getPassword()));
        return  appClientRepository.save(appClient);
    }

    @Override
    public BusinessOwner registerBusiness(BusinessRegisterDto business) {

            UserRoleEntity businessUserRole = this.userRoleService.getUserRoleByEnumName(UserRoleEnum.BUSINESS_USER);
            BusinessOwner businessOwner = this.modelMapper.map(business, BusinessOwner.class);
            businessOwner.setRoles(List.of(businessUserRole));
//            businessOwner.setPassword(this.passwordEncoder.encode(business.getPassword()));
           return businessOwnerRepository.save(businessOwner);


    }

    @Override
    public BusinessOwner saveUpdatedUser(BusinessOwner businessOwner) {
      return   this.businessOwnerRepository.save(businessOwner);

    }

    @Override
    public AppClient saveUpdatedUserClient(AppClient appClient) {
     return    this.appClientRepository.save(appClient);

    }

    @Override
    public UserEntity findUserById(Long userId) {


        Optional<UserEntity> byId = this.userRepository.findById(userId);

        if (byId.isPresent()) {
            return byId.get();
        } else {
            throw new NotFoundException("User not found");
        }


    }

    @Override
    public BusinessOwner findBusinessOwnerById(Long id) {
        Optional<BusinessOwner> businessOwner = this.businessOwnerRepository.findById(id);

        if(businessOwner.isPresent()) {
            return businessOwner.get();
        }
        else {
            throw new NotFoundException("Can not find business owner");
        }

    }

    @Override
    public UserEntity findUserByUsername(String username) {
        Optional<UserEntity> byUsername = this.userRepository.findByUsername(username);

        if(byUsername.isPresent()){
            return byUsername.get();
        }
        else {
            throw new NotFoundException("Can not find user with this username");
        }
    }

    @Override
    public boolean userExists(String username, String email) {
        Optional<UserEntity> byUsername = this.userRepository.findByUsername(username);
        Optional<UserEntity> byEmail = this.userRepository.findByEmail(email);

        return byUsername.isPresent() || byEmail.isPresent();

    }

    @Override
    public void deleteUser(Long id) {
        UserEntity user = findUserById(id);
//        expireUserSessions();

        userRepository.delete(user);


    }

    @Override
    public void deleteBusinessOwner(Long id) {
        Optional<BusinessOwner> user = this.businessOwnerRepository.findById(id);
        if(user.isPresent()) {

//
//            expireUserSessions();
            userRepository.delete(user.get());
        }
        else {
            throw new NotFoundException("Can not find current business owner");
        }

    }

    @Override
    public void deleteAppClient(Long id) {
        Optional<AppClient> user = this.appClientRepository.findById(id);
        if(user.isPresent()) {

            this.appClientRepository.save(user.get());
//            expireUserSessions();

            appClientRepository.delete(user.get());
        }
        else {
            throw new NotFoundException("Can not find current user");
        }
    }

    @Override
    public AppClient findAppClientById(Long clientId) {
        Optional<AppClient> user = this.appClientRepository.findById(clientId);
        if(user.isPresent()) {

            return user.get();
        }
        else {
            throw new NotFoundException("Can not find current user.");
        }
    }

    @Override
    public void findAndRemoveHobbyFromClientsRecords(Hobby hobby) {
        List<AppClient> all = this.appClientRepository.findAll();

        for (AppClient appClient : all) {
            appClient.getSaved_hobbies().remove(hobby);
            appClient.getHobby_matches().remove(hobby);
        }
    }

//    @Override
//    public void expireUserSessions() {
//                    SecurityContextHolder.clearContext();
//        }

    @Override
    public boolean businessExists(String businessName) {
        Optional<BusinessOwner> byBusinessName = this.businessOwnerRepository.findByBusinessName(businessName);
        return byBusinessName.isPresent();
    }

    @Override
    public AppClient findAppClientByUsername(String username) {
        //TODO CHECK EXCEPTION!
        return this.appClientRepository.findByUsername(username).orElseThrow();
    }

//    @Override
//    public BusinessOwner findCurrentUserBusinessOwner() {
//
//        Optional<BusinessOwner> user = this.businessOwnerRepository.findByUsername(findCurrentUsername());
//        if(user.isPresent()) {
//            return user.get();
//        }
//        else {
//            throw new NotFoundException("Can not find current business owner");
//        }
//
//    }

//    @Override
//    public AppClient findCurrentUserAppClient() {
//        Optional<AppClient> user = this.appClientRepository.findByUsername(findCurrentUsername());
//        if(user.isPresent()) {
//            return user.get();
//        }
//        else {
//            throw new NotFoundException("Can not find current user");
//        }
//    }


//    public String findCurrentUsername() {
//
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username = "";
//        if (principal instanceof UserDetails) {
//            username = ((UserDetails)principal).getUsername();
//        } else {
//            username = principal.toString();
//        }
//            return username;
//    }



}
