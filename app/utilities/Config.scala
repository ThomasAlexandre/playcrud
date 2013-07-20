package utilities

/*
 * Copyright (C) 2012 Thomas Alexandre
 */

case class Config(
  driver: String = "com.mysql.jdbc.Driver",
  dburl: String = "jdbc:mysql://localhost:3306/grails_ci",
  username: String = "root",
  password: String = "root",
  //baseDirectory: String = "/Users/thomas/projects/internal/stockapps")
  baseDirectory: String = "/Users/thomas/tmp/demo")
  
//case class Config(
//  driver: String = "org.h2.Driver",
//  dburl: String = "jdbc:h2:mem:devDb",
//  username: String = "sa",
//  password: String = "",
//  baseDirectory: String = "/tmp/grailsdemo")