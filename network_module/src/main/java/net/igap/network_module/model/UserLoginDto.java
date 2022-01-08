package net.igap.network_module.model;

import net.igap.network_module.AbstractObject;
import net.igap.network_module.IG_RPC;

import java.util.List;

public class UserLoginDto {

    public String phoneNumber;
    public String firstName;
    public String lastName;
    public String userName;
    public Long userId;
    public List<Long> smsNumbers;
    public String regex;
    public int verifyCodeDigitCount;
    public String authorHash;
    public boolean callMethodSupported;
    public long resendCodeDelay;
    public net.iGap.proto.ProtoUserRegister.UserRegisterResponse.Method methodpublicue;
    public String countryCode = "IR";

    public static UserLoginDto create(AbstractObject response) {
        IG_RPC.Res_User_Login res = (IG_RPC.Res_User_Login) response;
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.authorHash = res.authorHashR;
        userLoginDto.callMethodSupported = res.callMethodSupported;
        userLoginDto.countryCode = "IR";
        userLoginDto.userName = res.userNameR;
        userLoginDto.userId = res.userIdR;
        userLoginDto.smsNumbers = res.smsNumbers;
        userLoginDto.methodpublicue = res.methodValue;
        userLoginDto.resendCodeDelay = res.resendCodeDelay;
        userLoginDto.verifyCodeDigitCount = res.verifyCodeDigitCount;
        return userLoginDto;
    }

}
