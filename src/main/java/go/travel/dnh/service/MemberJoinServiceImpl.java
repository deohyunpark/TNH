package go.travel.dnh.service;

import go.travel.dnh.domain.member.MemberDTO;
import go.travel.dnh.repository.MemberJoinRepository;
import go.travel.dnh.validation.MemberjoinForm;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MemberJoinServiceImpl implements MemberJoinService {

    private final MemberJoinRepository memberJoinRepository;
    private final JavaMailSender mailSender;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private String answerNum;
    private int result;
    private String rawPwd;
    private String encodePwd;

    @Transactional
    public void joinMember(MemberjoinForm form){

        rawPwd = form.getMem_pwd();
        encodePwd = bCryptPasswordEncoder.encode(rawPwd);

       //성공로직
       MemberDTO memberDTO = new MemberDTO();
       memberDTO.setMem_id(form.getMem_id());
       memberDTO.setMem_pwd(encodePwd);
       memberDTO.setMem_name(form.getMem_name());
       memberDTO.setMem_phone(form.getMem_phone());
       memberDTO.setMem_gender(form.getMem_gender());
       memberDTO.setMem_birth(form.getMem_birth());

        memberJoinRepository.joinMember(memberDTO);

        int mno = memberDTO.getMno();
        memberJoinRepository.insertMemberRole(mno);
    }

    @Transactional
    public void joinSnsMember(MemberjoinForm form){

        rawPwd = form.getMem_pwd();
        encodePwd = bCryptPasswordEncoder.encode(rawPwd);

        //성공로직
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setMem_id(form.getMem_id());
        memberDTO.setMem_pwd(encodePwd);
        memberDTO.setMem_name(form.getMem_name());
        memberDTO.setMem_phone(form.getMem_phone());
        memberDTO.setMem_gender(form.getMem_gender());
        memberDTO.setMem_birth(form.getMem_birth());
        memberDTO.setMem_sns(true);

        memberJoinRepository.joinSnsMember(memberDTO);
        int mno = memberDTO.getMno();
        memberJoinRepository.insertMemberRole(mno);
    }

    public int findMember(String mem_id){
        return memberJoinRepository.readById(mem_id);
    }

    @Override
    public int findMemberbyPhone(int mem_phone) {
       return memberJoinRepository.readByPhone(mem_phone);
    }

    public void Sendmail(String mem_id){

        answerNum = randomNum();

        //이메일 보낼 문구
        String setFrom = "skfk6352@naver.com";
        String toMail = mem_id;
        String title = "회원가입 인증 이메일 입니다.";
        String content =
                "인증 번호는 " + answerNum + "입니다." +
                        "<br>" +
                        "해당 인증번호를 인증번호 확인란에 기입하여 주세요.";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);
            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(answerNum);
    }

    public String sendPwdMail(String mem_id){

        String randomPwd = randomNum(); //인코딩 전 비밀번호
        String mem_pwd;    // 인코딩 후 비밀번호

        //이메일 보낼 문구
        String setFrom = "skfk6352@naver.com";
        String toMail = mem_id;
        String title = "임시 비밀번호 입니다.";
        String content =
                "임시 비밀번호는" + randomPwd + "입니다." +
                        "<br>" +
                        "임시 비밀번호로 로그인한 뒤 반드시 마이페이지에서 비밀번호를 변경해주세요";
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content,true);
            mailSender.send(message);
        }catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("임시비밀번호" + randomPwd);

        mem_pwd = bCryptPasswordEncoder.encode(randomPwd);        // 비밀번호 인코딩
        memberJoinRepository.updatePwd(mem_id,mem_pwd);

        return randomPwd;

    }


    public String randomNum() {
        Random random = new Random();
        int num = 0;
        String stringNum = "";
        String resultNum = "";
        int size = 6;

        for (int i = 0; i < size; i++) {
            num = random.nextInt(10);
            stringNum = Integer.toString(num);
            resultNum += stringNum;
        }
        return resultNum;
    }

    public int mailNumCheck(String checkInput){

        if(checkInput.equals(answerNum)){
            result = 1;
        } else {
            result = 0;
        }
        return result;
    }

}
