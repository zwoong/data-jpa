package study.data_jpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.data_jpa.entity.Member;
import study.data_jpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberRepository memberRepository;

//  @GetMapping("/members/{id}")
//  public String findMember(@PathVariable("id") Long id) {
//    Member member = memberRepository.findById(id).get();
//    return member.getUsername();
//  }

  @GetMapping("/members/{id}")
  public String findMember2(@PathVariable("id") Member member) {
    return member.getUsername();
  }

  @GetMapping("/members")
  public Page<Member> list(Pageable pageable) {
    return memberRepository.findAll(pageable);
  }

//  @PostConstruct
  public void init() {
    for (int i = 0; i < 100; i++) {
      memberRepository.save(new Member("user" + i, i));
    }
  }
}
