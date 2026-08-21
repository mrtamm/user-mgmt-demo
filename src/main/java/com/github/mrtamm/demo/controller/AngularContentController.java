package com.github.mrtamm.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * This controller helps to serve static Angular files at any path, and therefore supports the page
 * reloads.
 */
@Controller
public class AngularContentController {

  @GetMapping("/{path:[^\\.]*}")
  public String forwardSingleLevel() {
    return "forward:/index.html";
  }

  @GetMapping("/**/{path:[^\\.]*}")
  public String forwardMultiLevel() {
    return "forward:/index.html";
  }
}