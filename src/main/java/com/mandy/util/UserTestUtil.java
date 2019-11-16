package com.mandy.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mandy.domain.SeckillUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MandyOu on 2019/11/14
 */
public class UserTestUtil {

    public static Logger logger = LoggerFactory.getLogger(UserTestUtil.class);


    private static void createUser(int count) throws SQLException, ClassNotFoundException, IOException {
        List<SeckillUser> users = new ArrayList<>();
        for(int i=0;i<count;i++) {
            SeckillUser user = new SeckillUser();
            user.setId(13000000000L+i);
            user.setLoginCount(1);
            user.setNickname("user"+i);
            user.setRegisterDate(new Date());
            user.setSalt("1a2b3c");
            user.setPassword(MD5Util.inputPassToDBPass("123456", user.getSalt()));
            users.add(user);
        }
        logger.info("create user");

        //插入数据库
//        Connection conn = DBUtil.getConn();
//        String sql = "insert into seckill_user(id,nickname,password,salt,register_date,login_count) values(?,?,?,?,?,?)";
//        PreparedStatement pstmt = conn.prepareStatement(sql);
//        for(int i = 0; i < users.size();i++){
//            SeckillUser user = users.get(i);
//            pstmt.setLong(1, user.getId());
//            pstmt.setString(2, user.getNickname());
//            pstmt.setString(3, user.getPassword());
//            pstmt.setString(4, user.getSalt());
//            pstmt.setTimestamp(5, new Timestamp(user.getRegisterDate().getTime()));
//            pstmt.setInt(6, user.getLoginCount());
//			pstmt.addBatch();
//        }
//        pstmt.executeBatch();
//        pstmt.close();
//        conn.close();
//        logger.info("insert to db");

        //登录，生成token
        String urlString = "http://localhost:8080/login/do_login";
        File file = new File("D:/tokens.txt");
        if(file.exists()){//如果该文件已经存在，则删除，重新更新文件
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file,"rw");
        file.createNewFile();
        raf.seek(0);
        for(int i = 0; i < users.size();i++){
            SeckillUser user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            OutputStream out = httpConn.getOutputStream();
            String params = "mobile="+user.getId()+"&password="+MD5Util.inputPassToFormPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = httpConn.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while((len = inputStream.read(buff)) >= 0){
                bout.write(buff,0,len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            JSONObject jsonObj = JSON.parseObject(response);
            String token = jsonObj.getString("data");
            logger.info("create token:" + user.getId());

            String row = user.getId()+","+token;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            logger.info("write to file:" + user.getId());
        }
        raf.close();
        logger.info("data initial over!!!");
    }

    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        createUser(5000);
    }
}
