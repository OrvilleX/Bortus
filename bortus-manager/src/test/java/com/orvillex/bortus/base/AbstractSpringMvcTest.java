package com.orvillex.bortus.base;

import com.orvillex.bortus.manager.AppRun;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@ActiveProfiles("unittest")
@SpringBootTest(classes = AppRun.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractSpringMvcTest {
    @Autowired
    private WebApplicationContext applicationContext;
    protected MockMvc mockMvc;
  
    @Before
    public void setup() {
      this.init();
    }

    protected void init() {
      this.mockMvc = MockMvcBuilders.webAppContextSetup(this.applicationContext).build();
      MockUserDetails userDetails = new MockUserDetails();
      UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, "123456");
      SecurityContextHolder.getContext().setAuthentication(token);
    }
}
