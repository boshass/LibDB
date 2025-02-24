import java.io.*;
import java.io.IOException;
import java.sql.*;
import oracle.jdbc.driver.*;

public class LibraryDB
{
    public static void main(String args[]) throws SQLException, IOException, InterruptedException
    {
        // Login
//        clearScreen();
//        Console console = System.console();
//        System.out.print("Enter your username: ");    // Your Oracle ID with double quote
//        String username = console.readLine();         // e.g. "98765432d"
//        System.out.print("Enter your password: ");    // Password of your Oracle Account
//        char[] password = console.readPassword();
//        String pwd = String.valueOf(password);
        String username, pwd;
//        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
//        System.out.print("Enter your username: ");
//        username = buffer.readLine();
//        System.out.print("Enter your password: ");
//        pwd = buffer.readLine();
        username = "\"21103213d\"";
        pwd = "hfnmexfb";

        System.out.println("Loading...");
        // Connection
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection conn =
                (OracleConnection)DriverManager.getConnection(
                        "jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms",username,pwd);
        clearScreen();

        int k = 0;
        while(k != -1){
            System.out.println("Choose the type of account to login.");
            System.out.println("1. Reader");
            System.out.println("2. Admin");
            System.out.println("-1 to exit");
            k = Integer.valueOf(readEntry("Input your choice: ")).intValue();
            if (k == 1){
                clearScreen();
                Reader reader = new Reader();
                if(reader.RLogin(conn)){
                    System.out.println(" ");
                }else continue;
                int knum = 0;
                while(knum != -1){
                    System.out.println("Choose the function you want to use.");
                    System.out.println("1. search books by Title");
                    System.out.println("2. search books by Author");
                    System.out.println("3. search books by Category");
                    System.out.println("4. find friend by book");
                    System.out.println("5. recommended books");
                    System.out.println("-1 to exit");
                    knum = Integer.valueOf(readEntry("Input your choice: ")).intValue();
                    if (knum == 1 || knum == 2 || knum == 3) {
                        clearScreen();
                        reader.SearchingBooks(conn, knum);
                    }
                    if (knum == 4){
                        clearScreen();
                        reader.ReaderFindFriend(conn);
                    }
                    if (knum == 5){
                        clearScreen();
                        reader.ReaderRecommendBook(conn);
                    }
                    if (knum == -1) {clearScreen(); break;}
                }
            }
            if(k == 2){
                Admin admin = new Admin();
                if (admin.ALogin(conn)){
                    int input = 0;
                    while (input != -1) {
                        System.out.println("1. Add reader");
                        System.out.println("2. Delete reader");
                        System.out.println("-1 to exit");
                        input = Integer.valueOf(readEntry("Input your choice: "));
                        if (input == 1) {
                            clearScreen();
                            admin.AdminAddUser(conn);
                        }
                        else if(input == 2) {
                            clearScreen();
                            admin.AdminDeleteUser(conn);
                        }
                        else if (input == -1) {clearScreen(); break;}
                    }
                }else continue;
            }
        }

//        // Prepare employee list
//        Statement stmt;
//        ResultSet rset;
//        String snum, namer;
//        int enumber=0;
//
//        // Prepare SQL for request
//        PreparedStatement prepareQuery = conn.prepareStatement(
//                "select * from book where isbn = ?");
//
//        while (enumber != -1)
//        {
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery("select isbn, title from book");
////            System.out.println("ENO" + " " + "ENAME");
//            while (rset.next())
//            {
//                namer = rset.getString(2);
//                if (!rset.wasNull())
//                {
//                    System.out.println(rset.getInt(1) + " " + namer);
//                }
//            }
//
//            // Get request
//            System.out.println();
//            snum = readEntry("ISBN: ");
//            enumber = Integer.valueOf(snum).intValue();
//
//            // Get result
//            prepareQuery.setInt(1, enumber);
//            rset = prepareQuery.executeQuery();
//
//            // Display result
//            while (rset.next())
//            {
//                System.out.flush();
////                System.out.println("ENO ENAME ZIO HDATE");
//                System.out.println(rset.getInt(1) + " " +
//                        rset.getString(2) + " " +
//                        rset.getString(3) + " " +
//                        rset.getString(4) + " " +
//                        rset.getString(5) + " " +
//                        rset.getInt(6));
//            }
//
//            // Continue?
//            System.out.println();
//            snum = readEntry(" Enter a number to continue or -1 to exit. ");
//            enumber = Integer.valueOf(snum).intValue();
//            clearScreen();
//        }

        // Exit the program
        conn.close();
        System.out.println("Bye! \n Press \'Enter\' to exit.");
        System.in.read();
        clearScreen();
    }

    // readEntry function -- Read input string
    static String readEntry(String prompt)
    {
        try
        {
            StringBuffer buffer = new StringBuffer();
            System.out.print(prompt);
            System.out.flush();
            int c = System.in.read();
            while (c != '\n' && c != -1)
            {
                buffer.append((char)c);
                c = System.in.read();
            }
            return buffer.toString().trim();
        }
        catch (IOException e)
        {
            return "";
        }
    }

    // clearScreen function -- clear screen
    static void clearScreen() throws IOException, InterruptedException
    {
        if (System.getProperty("os.name").contains("Windows"))
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        else
            System.out.print("\033[H\033[2J");
    }
}

class Reader{
    String accountID;
    Boolean RLogin(OracleConnection conn) throws IOException{
        PreparedStatement pst;
        ResultSet rst;
        try {
            pst = conn.prepareStatement("select accountid, password from reader where accountid = ? and password = ?");
            String accountID, password;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Reader name: ");
            accountID = br.readLine();
            System.out.print("Password: ");
            password = br.readLine();
            pst.setString(1, accountID);
            pst.setString(2, password);
            rst = pst.executeQuery();
            if (rst.next())
            {this.accountID = rst.getString("accountid"); System.out.println("You log in as " + rst.getString("accountid"));return true;}
            else {System.out.println("Wrong Reader name or password."); return false;}
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void SearchingBooks(OracleConnection conn, int knum) throws IOException{
        PreparedStatement pst;
        ResultSet rst;
        try{
            if (knum == 1) {
                pst = conn.prepareStatement("SELECT * FROM BOOK WHERE TITLE like ?");
                System.out.print("Enter a book's name to search books: ");
            }else if (knum == 2) {
                pst = conn.prepareStatement("SELECT * FROM BOOK WHERE AUTHOR like ?'");
                System.out.print("Enter a book's author to search books: ");
            }else {
                pst = conn.prepareStatement("SELECT * FROM BOOK WHERE CATEGORY like ?");
                System.out.print("Enter a book's category to search books: ");
            }
            String keyword;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            keyword = br.readLine();
            StringBuilder stringBuilder = new StringBuilder(keyword);
            stringBuilder.append('%');
            stringBuilder.insert(0,'%');
            keyword = stringBuilder.toString();
            pst.setString(1, keyword);
            rst = pst.executeQuery();
            if (rst == null)
                System.out.println("No result");
            while (rst.next())
            {
                System.out.flush();
                System.out.println();
                System.out.println("-------------------------------------------------------------------");
                System.out.println(rst.getInt(1) + " " +
                        rst.getString(2) + " " +
                        rst.getString(3) + " " +
                        rst.getString(4) + " " +
                        rst.getString(5) + " " +
                        rst.getInt(6));
                System.out.println("-------------------------------------------------------------------");
                System.out.println();
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }finally {
            System.out.println();
            System.out.println();
        }
    }

    void ReaderFindFriend(OracleConnection conn) throws IOException {
        PreparedStatement findFriend;
        ResultSet rst =null;
        try {
            //st = conn.createStatement();
            findFriend = conn.prepareStatement ("select READER.ACCOUNTID,READER.EMAIL from READER where READERID in (select OPERATION.READERID from OPERATION where ISBN = ?)");
            int isbn;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("ISBN: ");
            isbn = Integer.valueOf(br.readLine());
            findFriend.setInt(1,isbn);
            rst = findFriend.executeQuery();
            if(rst == null) {
                System.out.println("Find nobody");
            }
            while (rst.next())
            {System.out.println("Friend name: " + rst.getString("ACCOUNTID") + "   " +"Friend email: " + rst.getString("EMAIL"));}


        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    void ReaderRecommendBook(OracleConnection conn) throws IOException {
        PreparedStatement findbook;
        ResultSet rst =null;
        try {

            findbook = conn.prepareStatement ("SELECT book.ISBN, book.TITLE FROM BOOK WHERE book.CATEGORY IN (SELECT OPERATION.CATEGORY FROM OPERATION WHERE operation.ACCOUNTID = ?)");
            findbook.setString(1,accountID);
            rst = findbook.executeQuery();
            if(rst == null)
                System.out.println("Find nothing, please read more books first, thank you.");
            System.out.println("We recommend you to read: ");
            while (rst.next()) {
                System.out.println("BOOK ISBN: " + rst.getString("ISBN") + "   " + "BOOK TITLE: " + rst.getString("TITLE"));
            }

        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}
class Admin {
    Boolean ALogin(OracleConnection conn) throws IOException {
        PreparedStatement pst;
        ResultSet rst;
        try {
            pst = conn.prepareStatement("select admin_name, password from admin where admin_name = ? and password = ?");
            String adminName, password;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Admin name: ");
            adminName = br.readLine();
            System.out.print("Password: ");
            password = br.readLine();
            pst.setString(1, adminName);
            pst.setString(2, password);
            rst = pst.executeQuery();
            if (rst.next())
            {System.out.println("You login as " + rst.getString("admin_name"));return true;}
            else {System.out.println("Wrong Admin name or password."); return false;}
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    void AdminAddUser(OracleConnection conn) throws IOException {
        PreparedStatement 	addReader;
        ResultSet rst =null;
        try {
            //st = conn.createStatement();
            addReader = conn.prepareStatement ("INSERT INTO READER(ACCOUNTID,PASSWORD,EMAIL,STATUS,LEND) VALUES (?,?,?,?,?)");
            String accountId, password, email;
            int status = 1;
            int lend = 0;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("accountId: ");
            accountId = br.readLine();
            System.out.print("password: ");
            password = br.readLine();
            System.out.print("email: ");
            email = br.readLine();
            addReader.setString(1,accountId);
            addReader.setString(2,password);
            addReader.setString(3,email);
            addReader.setLong(4,status);
            addReader.setLong(5,lend);
            boolean result = addReader.execute();
            if(result = true) System.out.println("add success");
            else System.out.println("add fail");
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void AdminDeleteUser(OracleConnection conn) throws IOException {
        PreparedStatement 	deleteReader;
        ResultSet rst =null;
        try {
            deleteReader = conn.prepareStatement ("delete from READER where READER.ACCOUNTID = ? ");
            String accountId;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("accountId: ");
            accountId = br.readLine();
            deleteReader.setString(1,accountId);
            boolean result = deleteReader.execute();
            if(result = true) System.out.println("delete success");
            else System.out.println("delete fail");
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
