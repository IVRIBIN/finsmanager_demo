package com.tutu.finsmanager.web;

import com.tutu.finsmanager.core.*;
import com.tutu.finsmanager.dao.abstraction.BusinessEx;
import com.tutu.finsmanager.dao.abstraction.CompanyEx;
import com.tutu.finsmanager.dao.abstraction.EmployeeEx;
import com.tutu.finsmanager.dao.abstraction.UserCacheEx;
import com.tutu.finsmanager.model.Analytics.RequestAnalytics;
import com.tutu.finsmanager.model.Analytics.ResponseAnalytics;
import com.tutu.finsmanager.model.AppUser.RequestAppUser;
import com.tutu.finsmanager.model.AppUser.ResponseAppUser;
import com.tutu.finsmanager.model.Article.RequestArticle;
import com.tutu.finsmanager.model.Article.ResponseArticle;
import com.tutu.finsmanager.model.Business.ResponseBusiness;
import com.tutu.finsmanager.model.Business.BusinessForm;
import com.tutu.finsmanager.model.Comapny.CompanyForm;
import com.tutu.finsmanager.model.Comapny.ResponseCompany;
import com.tutu.finsmanager.model.Control.RequestControl;
import com.tutu.finsmanager.model.Control.ResponseControl;
import com.tutu.finsmanager.model.Counteragent.RequestCounteragent;
import com.tutu.finsmanager.model.Counteragent.ResponseCounteragent;
import com.tutu.finsmanager.model.Employee.EmployeeExImpl;
import com.tutu.finsmanager.model.Employee.EmployeeFilter;
import com.tutu.finsmanager.model.Employee.EmployeeForm;
import com.tutu.finsmanager.model.Employee.ResponseEmployee;
import com.tutu.finsmanager.model.Project.RequestProject;
import com.tutu.finsmanager.model.Project.ResponseProject;
import com.tutu.finsmanager.model.Requisite.RequestRequisite;
import com.tutu.finsmanager.model.Requisite.ResponseRequisite;
import com.tutu.finsmanager.registration.RegistrationForm;
import com.tutu.finsmanager.registration.RegistrationRequest;
import com.tutu.finsmanager.registration.RegistrationService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class WebController {
    private Logger logger = LoggerFactory.getLogger(WebController.class);
    @Autowired
    private FinsConfig finsConfig;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private UserCacheService userCacheService;
    @Autowired
    private BusinessService businessService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private AppUserServiceCore appUserServiceCore;
    @Autowired
    private CounteragentService counteragentService;
    @Autowired
    private RequisiteService requisiteService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ControlService controlService;
    @Autowired AnalyticsService analyticsService;

    @Autowired
    private MailDebuger mailDebuger;

    //Здесь вход на app
    @GetMapping({"/","/index"})
    public String greeting(Model model)
    {
        model.addAttribute("configPort",finsConfig.getPort());
        model.addAttribute("hrefLogin", "http://"+finsConfig.getPort()+"/login");
        return "indexView";
    }

    //Политика конфедициальности
    @GetMapping(value = "/privacy")
    public String GoToPrivacy(Model model) {
        try {
            return "indexPrivacyView";
        }catch (Exception ex){
            return "indexPrivacyView";
        }
    }

    //Политика конфедициальности
    @GetMapping(value = "/privacy-public-oferta")
    public String GoToPrivacyOferta(Model model) {
        try {
            return "indexPrivacyOfertaView";
        }catch (Exception ex){
            return "indexPrivacyOfertaView";
        }
    }


    //Переход на страницу Регистрации
    @GetMapping(value = "/registration")
    public String GoToUserRegistration(Model model){
        model.addAttribute("configPort",finsConfig.getPort());
        model.addAttribute("registrationform", new RegistrationForm());
        model.addAttribute("attrAClass", "input-group-text bg-white text-truncate text-muted");
        model.addAttribute("attrInputClass", "form-control-login input-login border-start-login ms-login fw-light");
        model.addAttribute("attrErrorMsgClass", "col-12 d-none");
        model.addAttribute("attrErrorMsgValue", "");
        model.addAttribute("href1", "http://"+finsConfig.getPort()+"/login");
        return "UserRegistration";
    }

    //Получение формы регистрации
    @RequestMapping(value = "/reg_confirm", method = RequestMethod.POST)
    public String GoToUserRegConfirm(@ModelAttribute RegistrationRequest request, Model model){
        String strPageName = "UserRegConfirm";
        try {
            if(request.getFirstName() == null || request.getLastName() == null || request.getEmail() == null || request.getPassword() == null || request.getApproval() == null){
                throw new Exception("mandatory fields");
            }
            if(request.getFirstName().compareTo("")==0 || request.getLastName().compareTo("")==0 || request.getEmail().compareTo("")==0 || request.getPassword().compareTo("")==0 || request.getApproval().compareTo("Y")==-1){
                throw new Exception("mandatory fields");
            }
            registrationService.register(request);
            model.addAttribute("configPort",finsConfig.getPort());
            model.addAttribute("href1", "http://"+finsConfig.getPort()+"/login");
        }catch (Exception e){
            String strErrorMsg = "Ошибка регистрации";
            if(e.toString().indexOf("mandatory fields") >= 0){
                strErrorMsg = "Заполните все поля";
            }
            if(e.toString().indexOf("Bad recipient address syntax") >= 0){
                strErrorMsg = "Ошибка email адреса";
            }
            if(e.toString().indexOf("email already taken") >= 0){
                strErrorMsg = "email адреса уже зарегестрирован";
            }
            model.addAttribute("attrAClass", "is-valid-icon-login input-group-text bg-white text-truncate text-muted");
            model.addAttribute("attrInputClass", "is-valid-icon-input-login form-control-login input-login border-start-login ms-login fw-light");
            model.addAttribute("attrErrorMsgClass", "col-12");
            model.addAttribute("attrErrorMsgValue", strErrorMsg);
            model.addAttribute("registrationform", new RegistrationForm());
            strPageName = "UserRegistration";
        }
        return strPageName;
    }


    //Точка отладки почты НАЧАЛО-------------------
    /*
    @GetMapping(value = "/maildebuger")
    public String GoToMailDebug(Model model){
        model.addAttribute("configPort",finsConfig.getPort());
        model.addAttribute("registrationform", new RegistrationForm());
        model.addAttribute("attrAClass", "input-group-text bg-white text-truncate text-muted");
        model.addAttribute("attrInputClass", "form-control-login input-login border-start-login ms-login fw-light");
        return "TestMail";
    }

    @RequestMapping(value = "/sendtestmail", method = RequestMethod.POST)
    public String GoToSendTestMail(@ModelAttribute RegistrationRequest request, Model model) {
        System.out.println("Run sendtestmail");
        mailDebuger.Send(request.getEmail());
        model.addAttribute("attrAClass", "input-group-text bg-white text-truncate text-muted");
        model.addAttribute("attrInputClass", "form-control-login input-login border-start-login ms-login fw-light");
        model.addAttribute("registrationform", new RegistrationForm());
        return "TestMail";
    }
    */

    //Точка отладки почты ЗАВЕРШЕНИЕ-------------------


    @GetMapping(value = "/recovery")
    public String GoToUserRecovery(Model model) {
        try {
            model.addAttribute("configPort",finsConfig.getPort());
            model.addAttribute("attrErrorFlg", "false");
            model.addAttribute("attrFieldClass", "form-control");
            model.addAttribute("registrationform", new RegistrationForm());
            return "UserRecovery";
        }catch (Exception ex){
            return "UserRecovery";
        }
    }

    @RequestMapping(value = "/recovery_confirm", method = RequestMethod.POST)
    public String GoToUserRecoveryConfirm(@ModelAttribute RegistrationRequest request, Model model) {
        try {
            registrationService.recovery(request);
            return "UserRecoveryConfirm";
        }catch (Exception ex){
            return "UserRecoveryConfirm";
        }
    }

    //Компания
    @GetMapping(value = "/company")
    public String GoToCompany(Model model) {
        try {
            model.addAttribute("attrUserRole", userCacheService.GetUserCache().getUserRole());//USER
            ModelSetter(model);
            return "companyView";
        }catch (Exception ex){
            //return "companyView";
            return "001";
        }
    }

    //Настройка пользователя
    @GetMapping(value = "/user_setting")
    public String GoToUserSetting(Model model) {
        try {
            //Рубим доступ SubUser
            if(userCacheService.GetUserCache().getUserRole().equals("USER")) {
                ModelSetter(model);
                return "userSettingsView";
            }else{
                return "001";
            }
        }catch (Exception ex){
            return "userSettingsView";
        }
    }

    //Контрагенты
    @GetMapping(value = "/business")
    public String GoToBusiness(Model model) {
        try {
            ModelSetter(model);
            return "businessView";
        }catch (Exception ex){
            return "businessView";
        }
    }

    //Бизнес
    @GetMapping(value = "/counteragents")
    public String GoToCounteragent(Model model) {
        try {
            ModelSetter(model);
            return "counteragentView";
        }catch (Exception ex){
            return "counteragentView";
        }
    }

    //Статьи
    @GetMapping(value = "/article")
    public String GoToArticle(Model model) {
        try {
            ModelSetter(model);
            return "articleView";
        }catch (Exception ex){
            return "articleView";
        }
    }

    @GetMapping(value = "/project")
    public String GoToProject(Model model) {
        try {
            ModelSetter(model);
            return "projectView";
        }catch (Exception ex){
            return "projectView";
        }
    }

    @GetMapping(value = "/control")
    public String GoToControl(Model model) {
        try {
            ModelSetter(model);
            return "controlView";
        }catch (Exception ex){
            return "controlView";
        }
    }

    //Анилитика
    @GetMapping(value = "/analytics")
    public String GoToAnalytics(Model model) {
        try {
                ModelSetter(model);
                return "analyticsView";
        }catch (Exception ex){
            return "View";
        }
    }

    //************************************************************************************************************//
    //********************************************************AJAX************************************************//
    //************************************************************************************************************//
    @RequestMapping(value = "/BusinessRepository", method = RequestMethod.GET)
    public @ResponseBody
    ResponseBusiness ResponseBusiness(@RequestParam String Operation)
    {
        ResponseBusiness result = new ResponseBusiness();
        try{
            switch(Operation){
                case "getUserBusinessList" : {
                    List<BusinessEx> businessList = businessService.GetBusinessList();
                    result.setBusinessList(businessList);
                }break;
                default:{
                    logger.info("WebController.ResponseBusiness: Неизвестная операция" + Operation);
                }
            }
            return result;
        }catch (Exception bus_rep_ex){
            logger.info("WebController.BusinessRepository -> Error: " + bus_rep_ex);
            return result;
        }
    }

    @PostMapping(value = "/formBusiness")
    public @ResponseBody
    ResponseBusiness FormBusiness(@RequestBody BusinessForm businessForm)
    {
        ResponseBusiness result = new ResponseBusiness();
        try{
            result = businessService.BusinessFormAction(businessForm);
            return result;
        }catch (Exception formBusiness_ex){
            logger.info("WebController.FormBusiness -> Error: " + formBusiness_ex);
            return result;
        }
    }


    @RequestMapping(value = "/requestCompany", method = RequestMethod.GET)
    public @ResponseBody
    ResponseCompany ResponseCompany(@RequestParam String Operation)
    {
        ResponseCompany result = new ResponseCompany();
        try{
            switch(Operation){
                case "getBusinessCompanyList" : {
                    List<CompanyEx> companyExList = companyService.GetCompanyList();
                    result.setCompanyExList(companyExList);
                }break;
                default:{
                    logger.info("WebController.ResponseCompany: Неизвестная операция" + Operation);
                }
            }
            return result;
        }catch (Exception comp_rep_ex){
            logger.info("WebController.ResponseCompany -> Error: " + comp_rep_ex);
            return result;
        }
    }

    @PostMapping(value = "/formCompany")
    public @ResponseBody
    ResponseCompany FormCompany(@RequestBody CompanyForm companyForm)
    {
        ResponseCompany result = new ResponseCompany();
        try{
            result = companyService.CompanyFormAction(companyForm);
            return result;
        }catch (Exception formCompany_ex){
            logger.info("WebController.FormCompany -> Error: " + formCompany_ex);
            return result;
        }
    }

    @PostMapping(value = "/formEmployee")
    public @ResponseBody
    ResponseEmployee FormEmployee(@RequestBody EmployeeForm employeeForm)
    {
        ResponseEmployee result = new ResponseEmployee();
        try{
            result = employeeService.EmployeeFormAction(employeeForm);
            return result;
        }catch (Exception formEmployee_ex){
            logger.info("WebController.FormEmployee -> Error: " + formEmployee_ex);
            return result;
        }
    }

    @PostMapping(value = "/requestEmployee")
    public @ResponseBody
    ResponseEmployee ResponseEmployee(@RequestBody EmployeeFilter employeeFilter)
    {
        String Operation = employeeFilter.getOperation();
        ResponseEmployee result = new ResponseEmployee();
        try{
            switch(Operation){
                case "getCompanyEmployeeList" : {
                    List<EmployeeEx> employeeExList = null;
                    List<EmployeeExImpl> employeeExImplList = null;
                    if(employeeFilter.isInitFlg()){
                        employeeService.EmployeeFormValidationFilter(employeeFilter);
                        result.setEmployeeFilter(employeeFilter);
                        if(employeeFilter.getValidFlg()){
                            employeeExImplList = employeeService.GetEmployeeList(employeeFilter);
                        }else{
                            employeeExImplList = employeeService.GetEmployeeList(employeeFilter);
                        }
                    }else{
                        employeeExImplList = employeeService.GetEmployeeList(employeeFilter);
                    }
                    result.setEmployeeExList(employeeExList);
                    result.setEmployeeExImplList(employeeExImplList);
                    Long employeeCount = employeeService.GetEmployeeCount();
                    result.setEmployeeCount(employeeCount);
                }break;
                case "getSelectedCompanyEmployeeCount" : {
                    Long selectedCount = employeeService.GetSelectedCount();
                    result.setSelectedCount(selectedCount);
                }break;
                case "getCompanyEmployeeListFilter" : {
                    logger.info("WebController.ResponseEmployee -> getCompanyEmployeeListFilter");
                }
                default:{
                    logger.info("WebController.ResponseEmployee: Неизвестная операция" + Operation);
                }
            }
            return result;
        }catch (Exception comp_rep_ex){
            logger.info("WebController.ResponseEmployee -> Error: " + comp_rep_ex);
            return result;
        }
    }

    @PostMapping(value = "/requestAppUser")
    public @ResponseBody
    ResponseAppUser RequestAppUser(@RequestBody RequestAppUser requestAppUser)
    {
        ResponseAppUser result = new ResponseAppUser();
        try{
            result = appUserServiceCore.AppUserFormAction(requestAppUser);
            result.setResultMsg("RequestAppUser");
            return result;
        }catch (Exception requestAppUserEx){
            logger.info("WebController.RequestAppUser -> Error: " + requestAppUserEx);
            return result;
        }
    }

    @PostMapping(value = "/requestCounteragent")
    public @ResponseBody
    ResponseCounteragent RequestCounteragent(@RequestBody RequestCounteragent requestContragent)
    {
        ResponseCounteragent result = new ResponseCounteragent();
        try{
            result = counteragentService.CounteragentFormAction(requestContragent);
            return result;
        }catch (Exception requestContragentEx){
            logger.info("WebController.RequestCounteragent -> Error: " + requestContragentEx);
            return result;
        }
    }

    @PostMapping(value = "/requestRequisite")
    public @ResponseBody
    ResponseRequisite RequestRequisite(@RequestBody RequestRequisite requestRequisite)
    {
        ResponseRequisite result = new ResponseRequisite();
        try{
            result = requisiteService.RequisiteFormAction(requestRequisite);
            return result;
        }catch (Exception requestRequisiteEx){
            logger.info("WebController.RequestRequisite -> Error: " + requestRequisiteEx);
            return result;
        }
    }

    @PostMapping(value = "/articleRequisite")
    public @ResponseBody
    ResponseArticle RequestArticle(@RequestBody RequestArticle requestArticle)
    {
        ResponseArticle result = new ResponseArticle();
        try{
            result = articleService.ArticleFormAction(requestArticle);
            return result;
        }catch (Exception requestArticleEx){
            logger.info("WebController.RequestArticle -> Error: " + requestArticleEx);
            return result;
        }
    }

    @PostMapping(value = "/projectRequisite")
    public @ResponseBody
    ResponseProject RequestProject(@RequestBody RequestProject requestProject)
    {
        ResponseProject result = new ResponseProject();
        try{
            result = projectService.ProjectFormAction(requestProject);
            return result;
        }catch (Exception requestProjectEx){
            logger.info("WebController.RequestProject -> Error: " + requestProjectEx);
            return result;
        }
    }

    @PostMapping(value = "/requestControl")
    public @ResponseBody
    ResponseControl RequestControl(@RequestBody RequestControl requestControl)
    {
        ResponseControl result = new ResponseControl();
        try{
            result = controlService.ControlFormAction(requestControl);
            return result;
        }catch (Exception requestControlEx){
            logger.info("WebController.RequestControl -> Error: " + requestControlEx);
            return result;
        }
    }

    @PostMapping(value = "/analyticsRequisite")
    public @ResponseBody
    ResponseAnalytics RequestAnalytics(@RequestBody RequestAnalytics requestAnalytics)
    {
        ResponseAnalytics result = new ResponseAnalytics();
        try{
            result = analyticsService.AnalyticsAction(requestAnalytics);
            return result;
        }catch (Exception requestAnalyticsEx){
            logger.info("WebController.RequestAnalytics -> Error: " + requestAnalyticsEx);
            return result;
        }
    }

    @PostMapping(value = "/usercache")
    public @ResponseBody
    UserCacheEx requestUserCacheEx()
    {
        return userCacheService.GetUserCache();
    }

    private void ModelSetter(Model model){
        //Установить доступы к настройкам пользователя, компании
        if(userCacheService.GetUserCache().getUserRole().equals("USER")==false){
            model.addAttribute("userSettingAccept",false);
            model.addAttribute("userCompanyAccept",false);
        }else{
            model.addAttribute("userSettingAccept",true);
            model.addAttribute("userCompanyAccept",true);
        }

        ModelAndView modelAndView = new ModelAndView();
        model.addAttribute("configPort",finsConfig.getPort());
        model.addAttribute("hrefBusiness", "http://"+finsConfig.getPort()+"/business");
        model.addAttribute("hrefCompany", "http://"+finsConfig.getPort()+"/company");
        model.addAttribute("hrefUserSettings", "http://"+finsConfig.getPort()+"/user_setting");
        model.addAttribute("hrefCounteragents", "http://"+finsConfig.getPort()+"/counteragents");
        model.addAttribute("hrefArticle", "http://" + finsConfig.getPort() + "/article");
        model.addAttribute("hrefProject", "http://" + finsConfig.getPort() + "/project");
        model.addAttribute("hrefControl", "http://" + finsConfig.getPort() + "/control");
        model.addAttribute("hrefAnalytics", "http://" + finsConfig.getPort() + "/analytics");
    }

}
