package hello.jdbc.repository;

// JDBC : Connection 을 파라미터로 넘기기

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
public class MemberRepositoryV2 {

    // DI 받아서 DataSource 사용
    private final DataSource dataSource;

    public MemberRepositoryV2(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // newR -> 계좌이체 서비스 로직에서 호출하는 메서드
    public Member findById(Connection con, String memberId) throws SQLException {

        String sql = "select * from member where member_id = ?";

        // Connection con = null; // 삭제
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // con = getConnection(); // 삭제 (이것 사용하면 새로운 커넥션이 맺어짐)
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery(); // ResultSet 반환, 값을 꺼내야 함
            if(rs.next()) { // next를 한 번은 호출해야 함, 데이터가 있는 경우
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }
            else { // 데이터가 없는 경우
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            // *** connection 은 여기에서 닫지 않는다 ***
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
            // JdbcUtils.closeConnection(con); // 서비스 계층에서 커넥션 종료해야 함
        }
    }

    // newU -> 계좌이체 서비스 로직에서 호출하는 메서드
    public void update(Connection con, String memberId, int money) throws SQLException {

        String sql = "update member set money=? where member_id=?";

        // Connection con = null; // 삭제
        PreparedStatement pstmt = null;

        try {
            // con = getConnection(); // 삭제
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            // *** connection 은 여기에서 닫지 않는다 ***
            JdbcUtils.closeStatement(pstmt);
            // JdbcUtils.closeConnection(con); // 서비스 계층에서 커넥션 종료해야 함
        }
    }




    // C
    public Member save(Member member) throws SQLException{

        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    // R
    public Member findById(String memberId) throws SQLException {

        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery(); // ResultSet 반환, 값을 꺼내야 함
            if(rs.next()) { // next를 한 번은 호출해야 함, 데이터가 있는 경우
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }
            else { // 데이터가 없는 경우
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    // U
    public void update(String memberId, int money) throws SQLException {

        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    // D
    public void delete(String memberId) throws SQLException {

        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.info("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    // 수정
    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }

    // 수정
    private Connection getConnection() throws SQLException {
       Connection con = dataSource.getConnection();
       log.info("get connection={}, class={}", con, con.getClass());
       return con;
    }
}
