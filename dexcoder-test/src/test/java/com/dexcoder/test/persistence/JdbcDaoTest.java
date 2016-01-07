package com.dexcoder.test.persistence;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dexcoder.commons.pager.Pager;
import com.dexcoder.commons.utils.StrUtils;
import com.dexcoder.dal.JdbcDao;
import com.dexcoder.dal.build.Criteria;
import com.dexcoder.dal.spring.page.PageControl;
import com.dexcoder.test.model.User;

/**
 * Created by liyd on 3/3/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class JdbcDaoTest {

    @Autowired
    private JdbcDao jdbcDao;

    //    @Autowired
    //    private DynamicDataSource dynamicDataSource;

    @Test
    public void before() {
        //插入测试数据
        for (int i = 0; i < 15; i++) {
            User user = new User();
            String loginName = i % 2 == 0 ? "selfly_" : "liyd_";
            user.setLoginName(loginName + i);
            user.setPassword("123456");
            user.setEmail(i + "javaer@live.com");
            user.setUserAge(10 + i);
            String userType = i % 3 == 0 ? "1" : "2";
            user.setUserType(userType);
            user.setGmtCreate(new Date());
            Long id = jdbcDao.insert(user);
            Assert.assertNotNull(id);
        }

    }

    @Test
    public void insert() {
        User user = new User();
        user.setLoginName("selfly_12");
        user.setPassword("123456");
        user.setEmail("javaer@live.com");
        user.setUserAge(18);
        user.setUserType("1");
        user.setGmtCreate(new Date());
        Long id = jdbcDao.insert(user);
        Assert.assertNotNull(id);
    }

    @Test
    public void insert2() {
        Criteria criteria = Criteria.insert(User.class).set("loginName", "selfly_b").set("password", "12345678")
            .set("email", "selflly@foxmail.com").set("userAge", 22).set("userType", "2").set("gmtCreate", new Date());
        Long id = jdbcDao.insert(criteria);
        Assert.assertNotNull(id);
        System.out.println("insert:" + id);
    }

    @Test
    public void save() {
        //先删除存在的测试数据
        jdbcDao.delete(User.class, -2L);
        User user = jdbcDao.get(User.class, -2L);
        Assert.assertNull(user);

        user = new User();
        user.setUserId(-2L);
        user.setLoginName("selfly-2");
        user.setPassword("123456");
        user.setEmail("javaer@live.com");
        user.setUserAge(18);
        user.setUserType("1");
        user.setGmtCreate(new Date());
        jdbcDao.save(user);

        user = jdbcDao.get(User.class, -2L);
        Assert.assertNotNull(user);
    }

    @Test
    public void save2() {
        //先删除存在的测试数据
        jdbcDao.delete(User.class, -3L);
        User user = jdbcDao.get(User.class, -3L);
        Assert.assertNull(user);

        Criteria criteria = Criteria.insert(User.class).set("userId", -3L).set("loginName", "selfly-3")
            .set("password", "123456").set("email", "selfly@foxmail.com").set("userAge", 18).set("userType", "2")
            .set("gmtCreate", new Date());
        jdbcDao.save(criteria);

        user = jdbcDao.get(User.class, -3L);
        Assert.assertNotNull(user);
    }

    @Test
    public void update() {
        //插入测试数据
        this.save();

        User user = new User();
        user.setUserId(-2L);
        user.setPassword("update");
        int i = jdbcDao.update(user);
        Assert.assertEquals(i, 1);

        user = jdbcDao.get(User.class, -2L);
        Assert.assertEquals("update", user.getPassword());
    }

    @Test
    public void update2() {
        //插入测试数据
        this.save();
        this.save2();
        Criteria criteria = Criteria.update(User.class).set("password", "update2")
            .where("userId", new Object[] { -2L, -3L });
        int i = jdbcDao.update(criteria);
        Assert.assertEquals(i, 1);

        User user = jdbcDao.get(User.class, -2L);
        Assert.assertEquals("update2", user.getPassword());

        user = jdbcDao.get(User.class, -3L);
        Assert.assertEquals("update2", user.getPassword());
    }

    @Test
    public void update3() {
        this.save();
        User user = jdbcDao.get(User.class, -2L);
        Integer oldUserAge = user.getUserAge();

        Criteria criteria = Criteria.update(User.class).set("[userAge]", "[userAge]+1")
            .where("userId", new Object[] { -2L });
        int i = jdbcDao.update(criteria);
        Assert.assertEquals(i, 1);

        user = jdbcDao.get(User.class, -2L);
        Assert.assertEquals(oldUserAge + 1L, (long) user.getUserAge());
    }

    @Test
    public void update4() {
        this.save();
        User user = jdbcDao.get(User.class, -2L);
        Integer oldUserAge = user.getUserAge();

        Criteria criteria = Criteria.update(User.class).set("{USER_AGE}", "{USER_AGE + 1}")
            .where("userId", new Object[] { -2L });
        int i = jdbcDao.update(criteria);
        Assert.assertEquals(i, 1);

        user = jdbcDao.get(User.class, -2L);
        Assert.assertEquals(oldUserAge + 1L, (long) user.getUserAge());
    }

    @Test
    public void testUpdate5() {
        this.save();
        User u = new User();
        u.setUserId(-2L);
        u.setLoginName("aabb");
        int i = jdbcDao.update(u, true);
        Assert.assertEquals(1, i);

        User user = jdbcDao.get(User.class, -2L);
        Assert.assertNotNull(user.getUserId());
        Assert.assertNotNull(user.getLoginName());
        Assert.assertNull(u.getPassword());
        Assert.assertNull(u.getEmail());
        Assert.assertNull(u.getGmtCreate());
        Assert.assertNull(u.getUserType());
        Assert.assertNull(u.getUserAge());
    }

    @Test
    public void get() {

        this.save();
        User u = jdbcDao.get(User.class, -2L);
        Assert.assertNotNull(u);
        Assert.assertEquals(-2L, (long) u.getUserId());
    }

    @Test
    public void get2() {
        this.save();
        Criteria criteria = Criteria.select(User.class).include("loginName");
        User u = jdbcDao.get(criteria, -2L);
        Assert.assertNotNull(u);
        Assert.assertNotNull(u.getLoginName());
        Assert.assertNull(u.getPassword());
        Assert.assertNull(u.getEmail());
        Assert.assertNull(u.getGmtCreate());
        Assert.assertNull(u.getUserType());
        Assert.assertNull(u.getUserAge());
    }

    @Test
    public void delete() {
        this.save();
        User u = new User();
        u.setUserId(-2L);
        u.setLoginName("selfly-2");
        u.setUserType("1");
        int i = jdbcDao.delete(u);
        Assert.assertEquals(i, 1);
    }

    @Test
    public void delete2() {
        this.save();
        this.save2();
        int i = jdbcDao.delete(Criteria.delete(User.class).where("userId", "in", new Object[] { -2L, -3L }));
        Assert.assertEquals(i, 2);
    }

    @Test
    public void delete3() {
        this.save();
        int i = jdbcDao.delete(User.class, -2L);
        Assert.assertEquals(i, 1);
    }

    @Test
    public void queryList() {
        this.save();
        this.save2();
        User u = new User();
        u.setUserType("1");
        List<User> users = jdbcDao.queryList(u);
        Assert.assertNotNull(users);
        for (User us : users) {
            Assert.assertEquals("1", us.getUserType());
        }
    }

    @Test
    public void queryList1() {
        this.save();
        this.save2();
        List<User> users = jdbcDao.queryList(User.class);
        Assert.assertNotNull(users);
        int count = jdbcDao.queryCount(User.class);
        Assert.assertEquals(users.size(), count);
    }

    @Test
    public void queryList2() {
        this.save();
        this.save2();
        PageControl.performPage(1, 2);
        Criteria criteria = Criteria.select(User.class).include("loginName", "userId").asc("userId");
        jdbcDao.queryList(criteria);
        Pager pager = PageControl.getPager();
        List<User> users = pager.getList(User.class);
        Assert.assertNotNull(users);

        int count = jdbcDao.queryCount(User.class);
        Assert.assertEquals(pager.getItemsTotal(), count);

        for (User us : users) {
            Assert.assertNotNull(us.getLoginName());
            Assert.assertNotNull(us.getUserId());
            Assert.assertNull(us.getEmail());
            Assert.assertNull(us.getPassword());
            Assert.assertNull(us.getUserAge());
            Assert.assertNull(us.getUserType());
            Assert.assertNull(us.getGmtCreate());
        }

        Assert.assertTrue(users.get(0).getUserId() < users.get(1).getUserId());
    }

    @Test
    public void queryList3() {
        this.save();
        this.save2();
        Criteria criteria = Criteria.select(User.class).exclude("loginName").where("userType", new Object[] { "1" })
            .asc("userAge").desc("userId");
        List<User> users = jdbcDao.queryList(criteria);
        Assert.assertNotNull(users);
        for (User us : users) {
            Assert.assertNull(us.getLoginName());
            Assert.assertNotNull(us.getEmail());
            Assert.assertNotNull(us.getUserId());
            Assert.assertNotNull(us.getUserType());
            Assert.assertNotNull(us.getUserAge());
            Assert.assertNotNull(us.getGmtCreate());
        }

        Assert.assertTrue(users.get(0).getUserId() > users.get(1).getUserId());
    }

    @Test
    public void queryList4() {
        this.save();
        this.save2();
        Criteria criteria = Criteria.select(User.class).where("loginName", "like", new Object[] { "%selfly%" });
        User user1 = new User();
        user1.setUserType("1");
        List<User> users = jdbcDao.queryList(user1, criteria.include("userId", "userType", "loginName"));
        Assert.assertNotNull(users);
        for (User us : users) {
            Assert.assertNull(us.getEmail());
            Assert.assertEquals("1", us.getUserType());
            Assert.assertTrue(StrUtils.indexOf(us.getLoginName(), "selfly") != -1);
        }
    }

    @Test
    public void queryCount() {
        User u = new User();
        u.setUserType("1");
        int count = jdbcDao.queryCount(u);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void queryCount2() {
        Criteria criteria = Criteria.select(User.class).where("userType", new Object[] { "1" });
        int count = jdbcDao.queryCount(criteria);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void querySingleResult() {
        this.save();
        User u = new User();
        u.setUserId(-2L);
        u = jdbcDao.querySingleResult(u);
        Assert.assertNotNull(u);
        Assert.assertEquals(-2L, (long) u.getUserId());
    }

    @Test
    public void querySingleResult2() {
        this.save();
        Criteria criteria = Criteria.select(User.class).where("userId", new Object[] { -2L });
        User u = jdbcDao.querySingleResult(criteria);
        Assert.assertNotNull(u);
        Assert.assertEquals(-2L, (long) u.getUserId());
    }

    @Test
    public void queryFunc() {
        Criteria criteria = Criteria.select(User.class).where("{length([loginName])}", ">", new Object[] { 8 });
        List<User> userList = jdbcDao.queryList(criteria);
        Assert.assertNotNull(userList);
        for (User u : userList) {
            Assert.assertTrue(u.getLoginName().length() > 8);
        }
    }

    @Test
    public void queryObject() {
        Criteria criteria = Criteria.select(User.class).addSelectFunc("max([userId])");
        Long userId = jdbcDao.queryObject(criteria);
        Assert.assertTrue(userId > 0);
    }

    @Test
    public void queryObject2() {
        Criteria criteria = Criteria.select(User.class).addSelectFunc("length([loginName]) loginNameLength", false,
            true);
        List<Map<String, Object>> mapList = jdbcDao.queryRowMapList(criteria);
        Assert.assertNotNull(mapList);
        for (Map<String, Object> map : mapList) {
            Assert.assertTrue(map.get("loginNameLength") != null);
            Assert.assertTrue(map.get("userId") != null);
        }
    }

    @Test
    public void queryRowMapList() {
        Criteria criteria = Criteria.select(User.class).addSelectFunc("distinct [loginName]");
        List<Map<String, Object>> mapList = jdbcDao.queryRowMapList(criteria);
        Assert.assertNotNull(mapList);
        for (Map<String, Object> map : mapList) {
            Assert.assertTrue(map.get("loginName") != null);
        }
    }

    @Test
    public void testBracket() {

        this.save();
        Criteria criteria = Criteria.select(User.class).where("userType", new Object[] { "1" }).begin()
            .and("loginName", new Object[] { "javaer@live.com" }).or("email", new Object[] { "javaer@live.com" }).end()
            .and("password", new Object[] { "123456" });
        User user = jdbcDao.querySingleResult(criteria);
        Assert.assertNotNull(user);
        Assert.assertTrue("javaer@live.com".equals(user.getLoginName()) || "javaer@live.com".equals(user.getEmail()));

        criteria = Criteria.select(User.class).where("userType", new Object[] { "1" }).begin()
            .and("loginName", new Object[] { "selfly-2" }).or("email", new Object[] { "selfly-2" }).end()
            .and("password", new Object[] { "123456" });
        user = jdbcDao.querySingleResult(criteria);
        Assert.assertNotNull(user);
        Assert.assertTrue("selfly-2".equals(user.getLoginName()) || "selfly-2".equals(user.getEmail()));
    }

    @Test
    public void testSelectSql() {

        this.save();
        List<Map<String, Object>> list = jdbcDao.queryRowMapListForSql("select * from USER where login_name = ?",
            new Object[] { "selfly-2" });
        for (Map<String, Object> map : list) {
            Assert.assertTrue("selfly-2".equals(map.get("loginName")));
        }
    }

    @Test
    public void testSelectSql2() {

        this.save();
        List<Map<String, Object>> list = jdbcDao
            .queryRowMapListForSql("select * from USER where login_name = 'selfly-2'");
        for (Map<String, Object> map : list) {
            Assert.assertTrue("selfly-2".equals(map.get("loginName")));
        }
    }

    @Test
    public void testSelectSql3() {

        PageControl.performPage(1, 10);
        jdbcDao
            .queryRowMapListForSql("select t.* ,t2.login_name lgName from USER t left join USER t2 on t.user_id=t2.user_id");
        Pager pager = PageControl.getPager();
        List<Map<String, Object>> list = (List<Map<String, Object>>) pager.getList();
        Assert.assertTrue(list.size() == 10);
    }

    @Test
    public void testUpdateSql() {
        this.save();
        int i = jdbcDao.updateForSql("update USER set login_name = ? where user_id = ?", new Object[] { "aaaa", -2L });
        Assert.assertTrue(i == 1);

        User user = jdbcDao.get(User.class, -2L);
        Assert.assertEquals("aaaa", user.getLoginName());
    }

    @Test
    public void testBatisSql() {
        List<Map<String, Object>> mapList = jdbcDao.queryRowMapListForSql("User.getUser");
        for (Map<String, Object> map : mapList) {
            System.out.println(map.get("user_id"));
            System.out.println(map.get("login_name"));
        }
    }

    @Test
    public void testBatisSql2() {
        User user = new User();
        user.setLoginName("selfly_a93");
        List<Map<String, Object>> mapList = jdbcDao.queryRowMapListForSql("User.getUser2", "user", new Object[] { user,
                "selfly_a93" });
        for (Map<String, Object> map : mapList) {
            System.out.println(map.get("user_id"));
            System.out.println(map.get("login_name"));
        }
    }

    @Test
    public void testBatisSql3() {
        User user = new User();
        user.setUserType("1");
        Object[] names = new Object[] { "selfly_a93", "selfly_a94", "selfly_a95" };
        List<Map<String, Object>> mapList = jdbcDao.queryRowMapListForSql("User.getUser", "params", new Object[] {
                user, names });
        for (Map<String, Object> map : mapList) {
            System.out.println(map.get("userId"));
            System.out.println(map.get("loginName"));
        }

    }

    //    @Test
    //    public void multiTableBook() {
    //        for (int i = 1; i < 51; i++) {
    //            Book book = new Book();
    //            book.setBookId((long) i);
    //            book.setBookName("测试book" + i);
    //            book.setGmtCreate(new Date());
    //            jdbcDao.save(book);
    //        }
    //        System.out.println("=================");
    //    }

    //    @Test
    //    public void multiTableChapter() {
    //        for (int i = 1; i < 51; i++) {
    //            Chapter chapter = new Chapter();
    //            chapter.setChapterId((long) i);
    //            chapter.setBookId(5L);
    //            chapter.setChapterName("章节一" + i);
    //            chapter.setGmtCreate(new Date());
    //            jdbcDao.save(chapter);
    //        }
    //        System.out.println("=================");
    //        for (int i = 51; i < 101; i++) {
    //            Chapter chapter = new Chapter();
    //            chapter.setChapterId((long) i);
    //            chapter.setBookId(6L);
    //            chapter.setChapterName("章节二" + i);
    //            chapter.setGmtCreate(new Date());
    //            jdbcDao.save(chapter);
    //        }
    //        System.out.println("=================");
    //    }
    //
    //    @Test
    //    public void multiTableChapterQuery() {
    //        Chapter chapter = new Chapter();
    //        chapter.setChapterId(22L);
    //        chapter.setBookId(5L);
    //        chapter = jdbcDao.querySingleResult(chapter);
    //        System.out.println(chapter.getChapterName());
    //        chapter = jdbcDao.querySingleResult(Criteria.create(Chapter.class)
    //                .where("chapterId", new Object[]{67L}).and("bookId", new Object[]{6L}));
    //        System.out.println(chapter.getChapterName());
    //    }
    //
    //    @Test
    //    public void multiTableChapterUpdate() {
    //        Chapter chapter = new Chapter();
    //        chapter.setChapterId(22L);
    //        chapter.setBookId(5L);
    //        chapter.setChapterName("更新后章节名");
    //        jdbcDao.update(chapter);
    //
    //        Chapter tmp = jdbcDao.querySingleResult(Criteria.create(Chapter.class)
    //                .where("chapterId", new Object[]{22L}).and("bookId", new Object[]{5L}));
    //        System.out.println(tmp.getChapterName());
    //    }
    //
    //    @Test
    //    public void multiTableChapterDelete() {
    //        Chapter chapter = new Chapter();
    //        chapter.setChapterId(23L);
    //        chapter.setBookId(5L);
    //        jdbcDao.delete(chapter);
    //
    //        Chapter tmp = jdbcDao.querySingleResult(Criteria.create(Chapter.class)
    //                .where("chapterId", new Object[]{23L}).and("bookId", new Object[]{5L}));
    //        Assert.assertNull(tmp);
    //    }
    //
    //    @Test
    //    public void dyDsInsert() {
    //        User user = new User();
    //        user.setLoginName("selfly");
    //        user.setGmtCreate(new Date());
    //        Long id = jdbcDao.insert(user);
    //
    //        User u = jdbcDao.get(Criteria.create(User.class)
    //                .include("userId", "loginName", "gmtCreate"), id);
    //        Assert.assertNotNull(u);
    //        System.out.println(u.getUserId() + " : " + u.getLoginName());
    //    }
    //
    //    @Test
    //    public void dyDsGet() {
    //        User u = jdbcDao.get(Criteria.create(User.class)
    //                .include("userId", "loginName", "gmtCreate"), 6L);
    //        Assert.assertNull(u);
    //    }
    //
    //    @Test
    //    public void dyDsGet2() {
    //        List<Map<String, String>> dsList = new ArrayList<Map<String, String>>();
    //
    //        Map<String, String> map = new HashMap<String, String>();
    //        map.put("id", "dataSource4");
    //        map.put("class", "org.apache.commons.dbcp.BasicDataSource");
    //        map.put("default", "true");
    //        map.put("weight", "10");
    //        map.put("mode", "rw");
    //        map.put("driverClassName", "com.mysql.dal.Driver");
    //        map.put("url",
    //                "dal:mysql://localhost:3306/db1?useUnicode=true&amp;characterEncoding=utf-8");
    //        map.put("username", "root");
    //        map.put("password", "");
    //        dsList.add(map);
    //
    //        int i = 0;
    //        while (i < 100) {
    //            User u = jdbcDao.get(
    //                    Criteria.create(User.class).include("userId", "loginName", "gmtCreate"), 6L);
    //            System.out.println(u == null ? "null" : u.getLoginName());
    //
    //            if (i == 70) {
    //                dynamicDataSource.initDataSources(dsList);
    //            }
    //            i++;
    //        }
    //    }
    //
    //    @Test
    //    public void testSql() {
    //        User user = new User();
    //        user.setLoginName("selfly38");
    //        user.setUserId(11L);
    //        jdbcDao.queryForSql("getUser", "user", user);
    //    }
    //
    //    @Test
    //    public void testSql2() {
    //        Map<String, Object> map = new HashMap<String, Object>();
    //        List<String> list = new ArrayList<String>();
    //        list.add("selfly");
    //        list.add("selfly37");
    //        list.add("selfly38");
    //
    //        map.put("list", list);
    //
    //        User user = new User();
    //        user.setLoginName("selfly39");
    //        user.setUserId(11L);
    //
    //        map.put("user", user);
    //
    //        jdbcDao.queryForSql("getUser2", map);
    //    }
    //
    //
    //    @Test
    //    public void testSql3() {
    ////        List<String> list = new ArrayList<String>();
    ////        list.add("selfly37");
    ////        list.add("selfly38");
    ////        List<Map<String, Object>> result = jdbcDao.queryForSql("queryUserList2", "list", list);
    ////        System.out.println(result.size());
    ////        System.out.println(result.iterator().next().get("login_name"));
    ////        System.out.println("===========");
    //    }
}
