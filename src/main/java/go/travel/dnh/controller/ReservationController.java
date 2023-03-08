package go.travel.dnh.controller;

import go.travel.dnh.domain.User.LoginUser;
import go.travel.dnh.domain.air.AirProductDTO;
import go.travel.dnh.domain.member.MemberDTO;
import go.travel.dnh.domain.reservation.AirReservationDTO;
import go.travel.dnh.domain.reservation.AirReservationListDTO;
import go.travel.dnh.domain.reservation.ReservationDetail;
import go.travel.dnh.service.AirProductService;
import go.travel.dnh.service.MemberLoginService;
import go.travel.dnh.service.ReservationService;
import go.travel.dnh.validation.ReservationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final AirProductService airProductService;
    private final ReservationService reservationService;
    private final MemberLoginService memberLoginService;

    /*예약정보 예약화면에 넘기기*/
    @PostMapping("/res-check")
    public String check(@ModelAttribute("resInfo") final ReservationInfo resInfo, Model m) {
        AirProductDTO outPro = airProductService.readRes(resInfo.getAir_from_check());
        AirProductDTO inPro = airProductService.readRes(resInfo.getAir_to_check());
        AirProductDTO onewayPro = airProductService.readRes(resInfo.getAir_oneway_check());

        System.out.println("ea "+resInfo.getEa());
        System.out.println("to "+resInfo.getAir_to());
        System.out.println("from "+resInfo.getAir_from());
        System.out.println("ano "+resInfo.getAir_oneway_check());

        if(resInfo.getAir_oneway_check()!=null){
            m.addAttribute("resInfo",resInfo);
            m.addAttribute("onewayPro",onewayPro);
            return "air/res-oneway";
        } else {

            System.out.println(resInfo.getEa());
            m.addAttribute("resInfo", resInfo);
            m.addAttribute("outPro", outPro);
            m.addAttribute("inPro", inPro);
            return "air/res-round";
        }
    }

    /*예약DB 저장*/
    @PostMapping("/reservation")
    public String reservation(AirReservationDTO dto, @ModelAttribute("reservationDetails") ReservationDetail reservationDetails, Model m, @AuthenticationPrincipal LoginUser loginUser, Authentication authentication) {
        airProductService.reservation(dto,reservationDetails,loginUser,authentication);

        MemberDTO memberDTO = memberLoginService.findMember(loginUser,authentication);
        List<AirReservationListDTO> revList = reservationService.selectMyRes(loginUser,authentication);

        m.addAttribute("memberDTO", memberDTO);
        m.addAttribute("revList", revList);
        return "order/bookingList";
    }

}
