package hello.jdbc.connection;

// 상수를 모아놓은 것이므로 생성을 하면 안됨
// 클래스를 abstract로 선언
public abstract class ConnectionConst {
    public static final String URL = "jdbc:h2:tcp://localhost/~/test"; // 규약
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";
}
