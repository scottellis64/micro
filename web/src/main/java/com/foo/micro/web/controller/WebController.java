package com.foo.micro.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {
   @RequestMapping("/")
   public String showMainPage() {
      return "index";
   }

   @RequestMapping("/login")
   public String showLoginPage() {
      return "login";
   }
}
