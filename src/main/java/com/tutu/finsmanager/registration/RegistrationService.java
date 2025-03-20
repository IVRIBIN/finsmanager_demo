package com.tutu.finsmanager.registration;


import com.tutu.finsmanager.appuser.AppUser;
import com.tutu.finsmanager.appuser.AppUserRepository;
import com.tutu.finsmanager.appuser.AppUserRole;
import com.tutu.finsmanager.appuser.AppUserService;
import com.tutu.finsmanager.core.FinsConfig;
import com.tutu.finsmanager.core.UserCacheService;
//import com.tutu.finsmanager.email.EmailSender;
import com.tutu.finsmanager.email.HostingMail;
import com.tutu.finsmanager.model.AppUser.RequestAppUser;
import com.tutu.finsmanager.registration.token.ConfirmationToken;
import com.tutu.finsmanager.registration.token.ConfirmationTokenRepository;
import com.tutu.finsmanager.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;

@Service
@AllArgsConstructor
public class RegistrationService {
    @Autowired
    FinsConfig finsConfig;
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    UserCacheService userCacheService;

    private final AppUserService appUserService;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;
    //private final EmailSender emailSender;
    private final HostingMail emailSender;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public String registerSub(RequestAppUser requestAppUser, Long mainUserId){
        Date date = new Date();
        boolean isValidEmail = emailValidator.test(requestAppUser.getEmail());

        if(!isValidEmail){
            throw  new IllegalStateException("email not valid");
        }

        String token = appUserService.signUpUser(
                new AppUser(mainUserId,
                            requestAppUser.getFirstName().toUpperCase(),
                            requestAppUser.getLastName().toUpperCase(),
                            requestAppUser.getMiddleName().toUpperCase(),
                            requestAppUser.getPhone(),
                            requestAppUser.getEmail(),
                            requestAppUser.getPassword(),
                            null,
                            date,
                            AppUserRole.SUB_USER,
                            "none",
                            "none",
                            "none",
                            "none"

                )
        );

        String link = "http://"+finsConfig.getPort()+"/api/v1/registration/confirm?token=" + token;
        emailSender.send(
                requestAppUser.getEmail(),
                buildEmail(requestAppUser.getFirstName(), link));

        return token;
    }

    public String register(RegistrationRequest request) {
        Date date = new Date();
        boolean isValidEmail = emailValidator.test(request.getEmail());

        if(!isValidEmail){
            throw  new IllegalStateException("email not valid");
        }

        String token = appUserService.signUpUser(
                new AppUser(null,
                            request.getFirstName(),
                            request.getLastName(),
                            request.getMiddleName(),
                            request.getPhone(),
                            request.getEmail(),
                            request.getPassword(),
                            request.getNewpassword(),
                            date,
                            AppUserRole.USER,
                            "full",
                            "full",
                            "full",
                            "full"
                )
        );

        //String link = "http://194.67.104.72:8081/api/v1/registration/confirm?token=" + token;
        String link = "http://"+finsConfig.getPort()+"/api/v1/registration/confirm?token=" + token;
        emailSender.send(
                request.getEmail(),
                buildEmail(request.getFirstName(), link));
        return token;
    }


    public String recovery(RegistrationRequest request){
        try {
            AppUser appUser = appUserRepository.GetUserByEmail(request.getEmail());
            String Result = "";

            if(appUser != null){
                List<ConfirmationToken> confirmationTokenList = confirmationTokenRepository.getRecovery(appUser.id);
                if(confirmationTokenList.size() <=3){
                    String token = "";
                    token = appUserService.recovery(appUser);
                    String encodedPassword = bCryptPasswordEncoder
                            .encode(request.getPassword());
                    appUserRepository.setNewPasswordAppUser(appUser.id,encodedPassword);
                    String link = "http://"+finsConfig.getPort()+"/api/v1/registration/recovery?token=" + token;
                    emailSender.send(
                            request.getEmail(),
                            buildRecoveryEmail(request.getFirstName(), link));
                }else{
                    Result = "В течении дня не более 3 запросов на восстановленеи пароля";
                }
            }

            return Result;
        }catch (Exception ex_recovery){
            return "";
        }
    }

    @Transactional
    public String confirmRecoveryToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if(confirmationToken.getTokenType().compareTo("recovery") !=0){
            throw new IllegalStateException("token not valid ");
        }

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("recovery token already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);

        appUserService.recoveryAppUser(
                confirmationToken.getAppUser().getEmail());
        return "<div style=\"background-color:#F2F3F7;padding:50px 20px;color:#5f76e8;border-radius:24px;font-family:'helvetica',sans-serif;font-size:17px;\"><h2>" + "Пароль успешно обновлен" + "</h2></div>";
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if(confirmationToken.getTokenType().compareTo("registration") !=0){
            throw new IllegalStateException("token not valid");
        }

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(
                confirmationToken.getAppUser().getEmail());
        userCacheService.SetUserCache(confirmationToken.getAppUser().getEmail());
        //return "<div style=\"background-color:#F2F3F7;padding:50px 20px;color:#5f76e8;border-radius:24px;font-family:'helvetica',sans-serif;font-size:17px;\"><h2>Статус активации личного кaбинета: <span style=\"padding:50px 20px;color:#35D073;\">Успешно</span></h2></div>";
        return "<div style=\"background: linear-gradient(#95D8F7, #4491FF);padding:20px 20px;color:#fff;border-radius:8px; display: inline-block;\">\n" +
                "    <h2 style=\"font-family:'helvetica',sans-serif;font-size:16px; font-weight: 100;\">Статус активации личного кaбинета: \n" +
                "        <span style=\"padding:5px 20px; border: 1px solid #fff; font-weight: 100;\">УСПЕШНО</span>\n" +
                "    </h2>\n" +
                "</div>";
    }

    public String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#897efc\">\n" +
                "\n" +
                "    <table role=\"presentation\" style=\"border-collapse:collapse;min-width:100%;width:100%!important;\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n" +
                "        <tbody>\n" +
                "            <tr>\n" +
                "                <td width=\"100%\" height=\"53\" bgcolor=\"#eee\" style=\"border-top-left-radius: 20px; border-top-right-radius: 20px; padding-top: 20px;\">\n" +
                "                    \n" +
                "                    <table role=\"presentation\" class=\"m_-6186904992287805515content\" style=\"border-collapse:collapse;max-width:580px;width:100%!important; text-align: center!important;\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\">\n" +
                "                        <tbody>\n" +
                "                            <tr>\n" +
                "                                <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "                                <td>\n" +
                "                            \n" +
                "                                    <table role=\"presentation\" style=\"border-collapse:collapse\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n" +
                "                                        <tbody>\n" +
                "                                            <tr>\n" +
                "                                                <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;text-decoration:none;vertical-align:top;display:inline-block; margin-bottom: 20px;\">\uD83D\uDCC1 <a href=\"#\" style=\"text-decoration: none; color:#333\">FinFolder</a></span>\n" +
                "                                            </tr>\n" +
                "                                        </tbody>\n" +
                "                                    </table>\n" +
                "                            \n" +
                "                                </td>\n" +
                "                                <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "                            </tr>\n" +
                "                        </tbody>\n" +
                "                    </table>\n" +
                "\n" +
                "                    <table role=\"presentation\" style=\"border-collapse:collapse;max-width:580px; border-radius: 8px;\" bgcolor=\"#897efc\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\">\n" +
                "                        <tbody>\n" +
                "                            \n" +
                "                            <tr>\n" +
                "                                <td width=\"40\" valign=\"middle\" style=\"border-radius: 8px; background: linear-gradient(#95D8F7, #4491FF);\">\n" +
                "                                    <table role=\"presentation\" style=\"border-collapse:collapse;\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n" +
                "                                        <tbody>\n" +
                "                                            \n" +
                "                                            <tr>\n" +
                "                                                <td style=\"font-size:25px;line-height:1.315789474; padding-left:10px; padding-top: 20px;\">\n" +
                "                                                    <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#fff;text-decoration:none;vertical-align:top;display:inline-block; margin-bottom: 10px;\">Подтвердите адрес электронной почты</span>\n" +
                "                                                </td>\n" +
                "                                            </tr>\n" +
                "                                            <tr>\n" +
                "                                                <td style=\"font-size:14px;line-height:1.315789474; padding-left:10px; padding-bottom: 10px;\">\n" +
                "                                                    <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:100;color:#fff;text-decoration:none;vertical-align:top;display:inline-block; margin-bottom: 10px;\">Поздравляем Вас с успешной регистрации на сайте <a href=\"#\" style=\"text-decoration: none; color: #fff\">finfolder.ru</a></span>\n" +
                "                                                </td>\n" +
                "                                            </tr>\n" +
                "                                        </tbody>\n" +
                "                                    </table>\n" +
                "                                </td>\n" +
                "                            </tr>\n" +
                "                        </tbody>\n" +
                "                    </table>\n" +
                "            \n" +
                "          \n" +
                "      \n" +
                "                    <table role=\"presentation\" class=\"m_-6186904992287805515content\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\">\n" +
                "                        <tbody>\n" +
                "                            <tr>\n" +
                "                                <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "                                <td>\n" +
                "                            \n" +
                "                                    <table role=\"presentation\" style=\"border-collapse:collapse\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n" +
                "                                        <tbody><tr>\n" +
                "                                        <td width=\"100%\" height=\"10\" bgcolor=\"#fff\"></td>\n" +
                "                                        </tr>\n" +
                "                                    </tbody></table>\n" +
                "                            \n" +
                "                                </td>\n" +
                "                                <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "                            </tr>\n" +
                "                        </tbody>\n" +
                "                    </table>\n" +
                "\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "        </tbody>\n" +
                "    </table>\n" +
                "    \n" +
                "    \n" +
                "\n" +
                "    <table role=\"presentation\" style=\"border-collapse:collapse;min-width:100%;width:100%!important;\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n" +
                "        <tbody>\n" +
                "            <tr>\n" +
                "\n" +
                "                <td width=\"100%\" height=\"53\" bgcolor=\"#eee\" style=\"padding-top: 20px;\">      \n" +
                "                  \n" +
                "                    <table role=\"presentation\" class=\"m_-6186904992287805515content\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\">\n" +
                "                        <tbody>\n" +
                "                            \n" +
                "                            <tr>\n" +
                "                                <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "                                <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:14px;line-height:1.315789474;max-width:560px; color: #b1b4b6\">\n" +
                "                            \n" +
                "                                <p style=\"Margin:0 0 20px 0;font-size:16px;line-height:25px;color:#646464\">Здравствуйте, "+ name + " \uD83D\uDE42</p>\n" +
                "                                <p style=\"Margin:0 0 20px 0;font-size:16px;line-height:25px;color:#646464\">\uD83E\uDD1D Спасибо, что зарегистрировались. Пожалуйста, нажмите на ссылку ниже, чтобы активировать свой аккаунт: </p>\n" +
                "                                <blockquote style=\"Margin:0 0 20px 0;border-left:4px solid #32087a;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px; border-radius: 4px;\">\n" +
                "                                    <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#fff\"> \n" +
                "                                        <a href=\"" + link + "\"style=\"background:#DEDDF0;border-color:#DEDDF0;border-radius:50px;border-style:solid;border-width:10px 20px 10px 20px;color:#333;display:inline-block;font-family:'arial' , 'helvetica neue' , 'helvetica' , sans-serif;font-size:16px;font-style:normal;font-weight:normal;line-height:22px;text-align:center;text-decoration-line:none !important;width:auto\" >Активировать</a> \n" +
                "                                    </p>\n" +
                "                                </blockquote>\n" +
                "                                <p>❕Срок действия ссылки истекает через 15 минут. Если Вы не регистрировались, то проигнорируйте пожалуйста это письмо.</p>\n" +
                "                                <p>❔Если Вам нужна помощь, обратитесь в Службу Поддержки</p>        \n" +
                "                            </td>\n" +
                "                            <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "                            </tr>\n" +
                "                            <tr>\n" +
                "                                <td height=\"30\"><br></td>\n" +
                "                            </tr>\n" +
                "                        </tbody>\n" +
                "                    </table>\n" +
                "\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "\n" +
                "        </tbody>\n" +
                "        \n" +
                "    </table>\n" +
                "\n" +
                "\n" +
                "    \n" +
                "    <table role=\"presentation\" style=\"border-collapse:collapse;min-width:100%;width:100%!important;\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n" +
                "        <tbody>\n" +
                "            <tr>\n" +
                "\n" +
                "                <td width=\"100%\" height=\"53\" bgcolor=\"#eee\" style=\"border-bottom-left-radius: 20px; border-bottom-right-radius: 20px;\">      \n" +
                "      \n" +
                "                    <table role=\"presentation\" class=\"m_-6186904992287805515content\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\">\n" +
                "                        <tbody>\n" +
                "                        <tr>\n" +
                "                            <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "                            <td>\n" +
                "                                <table role=\"presentation\" style=\"border-collapse:collapse\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n" +
                "                                    <tbody>\n" +
                "                                        <tr>\n" +
                "                                            <td width=\"100%\" height=\"2\" bgcolor=\"#646464\"></td>\n" +
                "                                        </tr>\n" +
                "                                    </tbody>\n" +
                "                                </table>\n" +
                "                            </td>\n" +
                "                            <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "                        </tr>\n" +
                "                        </tbody>\n" +
                "                     </table>\n" +
                "            \n" +
                "            \n" +
                "            \n" +
                "                    <table role=\"presentation\" class=\"m_-6186904992287805515content\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\">\n" +
                "                        <tbody>\n" +
                "                            <tr>\n" +
                "                                <td height=\"30\"><br></td>\n" +
                "                            </tr>\n" +
                "                            <tr>\n" +
                "                                <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "                                \n" +
                "                                <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:14px;line-height:1.315789474;max-width:560px; color: #b1b4b6\">\n" +
                "                                \n" +
                "                                    <p style=\"Margin:0 0 20px 0;font-size:14px;line-height:25px;color:#333\">\n" +
                "                                        \uD83D\uDD0E Вы получили это письмо, потому что прошли регистрацию на сайте finfolder.ru.\n" +
                "                                        Ознакомиться с пользовательским соглашением можно по \n" +
                "                                        <a href=\"#\" style=\"color: #333;\">ссылке</a>.\n" +
                "                                    </p>\n" +
                "\n" +
                "                                    <p style=\"Margin:0 0 20px 0;font-size:14px;line-height:25px;color:#333\"> \n" +
                "                                        <a href=\"http://localhost:8080/api/v1/registration/recovery?token=84090c59-27c4-4cc0-91f9-8ed3ed03015e\" style=\"color: #333;\">Отписаться от рассылки</a> \n" +
                "                                    </p>\n" +
                "\n" +
                "                                    Успешного использования сервиса для учета финансовых операций \uD83D\uDC4D <a href=\"#\" style=\"color: #b1b4b6;\">finfolder.ru</a>  \n" +
                "                                    <span>С уважением, Служба Поддержки FF. </span>    \n" +
                "                                </td>\n" +
                "\n" +
                "                                <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "                            </tr>\n" +
                "                            <tr>\n" +
                "                                <td height=\"30\"><br></td>\n" +
                "                            </tr>\n" +
                "                        </tbody>\n" +
                "                    </table>\n" +
                "\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "\n" +
                "        </tbody>\n" +
                "\n" +
                "    </table>\n" +
                "\n" +
                "</div>";
    }


    private String buildEmailOld1(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Подтвердите свой адрес электронной почты</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Привет " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Спасибо, что зарегистрировались. Пожалуйста, нажмите на ссылку ниже, чтобы активировать свой аккаунт: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Активируй сейчас</a> </p></blockquote>\n Срок действия ссылки истекает через 15 минут. <p>Скоро увидимся</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

    private String buildRecoveryEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Подтвердите смены пароля</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Привет " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Для смены пароля перейдите по ссылке: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Смена пароля</a> </p></blockquote>\n Срок действия ссылки истекает через 15 минут. Если вы не меняли пароль своего аккаунта, проигнорируйте это сообщение." +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}