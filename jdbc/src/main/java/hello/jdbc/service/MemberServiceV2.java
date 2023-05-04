package hello.jdbc.service;

// transaction - 파라미터 연동, 풀을 고려한 종료

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    // 계좌이체 로직
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        // 비즈니스 로직 시작할 때 커넥션 얻어야 함
        Connection con = dataSource.getConnection();
        try {
            con.setAutoCommit(false); // 트랜잭션 시작

            bizLogic(con, fromId, toId, money); // 비즈니스 로직

            con.commit(); // 정상적으로 실행되었다면 커밋

        } catch (Exception e) { // 예외 발생 (실패) 시 롤백
            con.rollback();
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }

    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId); // 커넥션 넘기기 추가
        Member toMember = memberRepository.findById(con, toId); // 커넥션 넘기기 추가

        memberRepository.update(con, fromId, fromMember.getMoney()- money); // 커넥션 넘기기 추가
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney()+ money); // 커넥션 넘기기 추가
    }

    private void release(Connection con) {
        if(con != null) {
            try {
                con.setAutoCommit(true); // 커넥션 풀을 고려하여 오토커밋
                con.close();
            } catch(Exception e) {
                log.info("error", e);
            }
        }
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

}
