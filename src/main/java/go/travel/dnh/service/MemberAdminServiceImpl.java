package go.travel.dnh.service;

import go.travel.dnh.domain.member.MemberDTO;
import go.travel.dnh.repository.MemberAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberAdminServiceImpl implements MemberAdminService {

    private final MemberAdminRepository memberAdminRepository;

    @Override
    public List<MemberDTO> getMemberList() {
       List<MemberDTO> memberList = memberAdminRepository.getMemberList();
       return memberList;
    }

    @Override
    @Transactional
    public void deleteMember(Integer mno){
        memberAdminRepository.deleteMember(mno);
        memberAdminRepository.deleteMemberRole(mno);
    }
}