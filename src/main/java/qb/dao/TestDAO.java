package qb.dao;

import qb.entities.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Created by sdanilov on 07.09.2015.
 */
@Service
public class TestDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public ArrayList<Test> getTempList(String user){
        ArrayList <Test> tmp = new ArrayList<Test>();
        for (int i = 0; i < 10; i++){
            Test testI = new Test();
            testI.setName(user);
            testI.setId(Integer.toString(i));
            tmp.add(testI);
        }

        return tmp;
    }
}
