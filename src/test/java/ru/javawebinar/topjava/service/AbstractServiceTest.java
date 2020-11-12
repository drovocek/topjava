package ru.javawebinar.topjava.service;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.junit.rules.Stopwatch;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.ActiveDbProfileResolver;
import ru.javawebinar.topjava.TimingRules;

import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.util.ValidationUtil.getRootCause;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml",
        "classpath:spring/testcache.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
@ActiveProfiles(resolver = ActiveDbProfileResolver.class)
abstract public class AbstractServiceTest {

    public static CacheManager cacheManager;

    @Autowired
    public static LocalContainerEntityManagerFactoryBean gg;

    @Autowired
    private Environment environment;

    @ClassRule
    public static ExternalResource summary = TimingRules.SUMMARY;

    @Rule
    public Stopwatch stopwatch = TimingRules.STOPWATCH;

    //  Check root cause in JUnit: https://github.com/junit-team/junit4/pull/778
    public <T extends Throwable> void validateRootCause(Runnable runnable, Class<T> rootExceptionClass) {
        assertThrows(rootExceptionClass, () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw getRootCause(e);
            }
        });
    }

    public boolean containProfiles(String... profile) {
        return List.of(environment.getActiveProfiles()).containsAll(List.of(profile));
    }

//    @Autowired
//    protected JpaUtil jpaUtil;

//    @Before
//    public void setUp() {
//        cacheManager.getCache("users").clear();
//        jpaUtil.clear2ndLevelHibernateCache();
//    }

    @BeforeClass
    public static void changeProp(){
        cacheManager = new NoOpCacheManager();
        System.out.println(cacheManager);
//        gg.getJpaPropertyMap().computeIfPresent("hibernate.cache.use_second_level_cache",(x,y)-> "true");
    }
}