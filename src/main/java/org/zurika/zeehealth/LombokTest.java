package org.zurika.zeehealth;

import org.zurika.zeehealth.model.User;

public class LombokTest {
    public static void main(String[] args) {
        User user = new User();
        user.setUsername("testuser");
        System.out.println(user.getUsername());
    }
}
