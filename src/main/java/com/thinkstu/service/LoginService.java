package com.thinkstu.service;

import java.util.*;

public interface LoginService {

    Map<String, String> login(String username, String password) throws Exception;

    String loginForEmpty(String username, String password) throws Exception;
}
